package com.changgou.canal.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.canal.mq.queue.TopicQueue;
import com.changgou.canal.mq.send.TopicMessageSender;
import com.changgou.content.feign.ContentFeign;
import com.changgou.user.pojo.Content;
import com.xpand.starter.canal.annotation.*;
import entity.Message;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@CanalEventListener
public class CanalDataEventListener {
    @Autowired
    private ContentFeign contentFeign;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TopicMessageSender topicMessageSender;

    /**
     * 新增监听
     *
     * @param eventType 变更操作的类型
     * @param rowData   此次变更的数据
     */
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
//        rowData.getBeforeColumnsList():数据变更前的内容
//        rowData.getAfterColumnsList():数据变更后的内容
        System.out.println("--------新增监听--------");
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println(column.getName() + ":" + column.getValue());
        }
    }

    /**
     * 更新监听
     *
     * @param eventType
     * @param rowData
     */
    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        System.out.println("--------更新监听--------");
        int i = 0;
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            //获取修改前的数据
            CanalEntry.Column beforeColumns = rowData.getBeforeColumns(i);
            //如果修改了字段
            if (!beforeColumns.getValue().equals(column.getValue())) {
                System.out.println("更新了字段:" + column.getName() + "   ");
                System.out.println(beforeColumns.getValue() + "-->" + column.getValue());
            }
            i++;
        }
    }

    /**
     * 删除监听
     *
     * @param eventType
     * @param rowData
     */
    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        System.out.println("--------删除监听--------");
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println(column.getName() + ":" + column.getValue());
        }
    }

    /**
     * 自定义监听
     * destination: 必须和canal.properties配置文件中的canal.destination属性名字相同
     * schema: 监听的数据库
     * table: 监听的表
     * eventType: 监听的操作类型
     *
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example", schema = "changgou_content", table = "tb_content", eventType = {CanalEntry.EventType.INSERT, CanalEntry.EventType.UPDATE, CanalEntry.EventType.DELETE})
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        System.out.println("--------广告监听--------");
        String categoryId = "";
        //新增
        if (eventType == CanalEntry.EventType.INSERT) {
            System.out.println("--------广告新增-------");
            categoryId = rowData.getAfterColumns(1).getValue();
            //修改
        } else if (eventType == CanalEntry.EventType.UPDATE) {
            categoryId = rowData.getAfterColumns(1).getValue();

            String categoryIdBefore = rowData.getBeforeColumns(1).getValue();

            //如果广告类别修改了
            if (!categoryIdBefore.equals(categoryId)) {
                Result<List<Content>> contents = contentFeign.findByCategoryId(Long.valueOf(categoryIdBefore));
                if (contents.getData() != null) {
                    //1 StringRedisTemplate.opsForValue().* //操作String字符串类型
                    //2 StringRedisTemplate.delete(key/collection) //根据key/keys删除
                    //3 StringRedisTemplate.opsForList().*  //操作List类型
                    //4 StringRedisTemplate.opsForHash().*  //操作Hash类型
                    //5 StringRedisTemplate.opsForSet().*  //操作set类型
                    //6 StringRedisTemplate.opsForZSet().*  //操作有序set
                    stringRedisTemplate.boundValueOps("content_" + categoryIdBefore).set(JSON.toJSONString(contents.getData()));
                }
            }
            //删除
        } else {
            categoryId = rowData.getBeforeColumns(1).getValue();
        }
        Result<List<Content>> contents = contentFeign.findByCategoryId(Long.valueOf(categoryId));
        if (contents.getData() != null) {
            stringRedisTemplate.boundValueOps("content_" + categoryId).set(JSON.toJSONString(contents.getData()));
        }
    }

    @ListenPoint(destination = "example", schema = "changgou_goods", table = {"tb_spu"}, eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.DELETE})
    public void onEventCustomSpu(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        if (eventType==CanalEntry.EventType.UPDATE){
            System.out.println("--------商品更新-------");
        }else if (eventType==CanalEntry.EventType.DELETE){
            System.out.println("--------商品删除-------");
        }
        //操作类型
        int number = eventType.getNumber();
        //操作的数据
        String id = getColumn(rowData, "id");
        //封装Message
        Message message = new Message(number, id, TopicQueue.TOPIC_QUEUE_SPU, TopicQueue.TOPIC_EXCHANGE_SPU);
        topicMessageSender.sendMessage(message);
    }

    public String getColumn(CanalEntry.RowData rowData, String name) {
        //操作后的数据
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            String columnName = column.getName();
            if (columnName.equalsIgnoreCase(name)) {
                return column.getValue();
            }
        }
        //操作前的数据
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            String columnName = column.getName();
            if (columnName.equalsIgnoreCase(name)) {
                return column.getValue();
            }
        }
        return null;
    }
}
