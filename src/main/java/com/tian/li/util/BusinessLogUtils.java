package com.tian.li.util;


import com.tian.li.annotation.LogRecord;
import com.tian.li.entity.AppUser;
import com.tian.li.entity.BizLogEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: lixiaotian
 * @date: 2021/2/26 11:41
 * @description: 业务变动日志工具类
 */
public class BusinessLogUtils<T> {

    /**
     * 远程调用数据字典接口的方法
     */
    private static final String GET_DIC_METHOD = "getDictionaryMap";

    /**
     * 日志存入mongodb默认的集合
     */
    private static final String DEFAULT_COLLECTION_NAME = "newsee_bussiness_log";

    private T feginClientClass;

    private MongoTemplate mongoTemplate;

    private Long userId;

    private String userName;

    private String operateType;

    private Long dataId;

    private Long enterpriseId;

    private Long precictId;

    private String collectionName;

    private int saveRemark;


    private BusinessLogUtils(T feginClientClass, MongoTemplate mongoTemplate, Long userId, String userName, String operateType, Long dataId, Long enterpriseId, Long precictId,
                             String collectionName, int saveRemark) {
        this.feginClientClass = feginClientClass;
        this.mongoTemplate = mongoTemplate;
        this.userId = userId;
        this.userName = userName;
        this.operateType = operateType;
        this.dataId = dataId;
        this.enterpriseId = enterpriseId;
        this.precictId = precictId;
        this.collectionName = collectionName;
        this.saveRemark = saveRemark;
    }


    public static class Builder<T> {
        private T feginClientClass;

        private MongoTemplate mongoTemplate;

        private Long userId;

        private String userName;

        private String operateType;

        private Long dataId;

        private Long enterpriseId;

        private Long precictId;

        private String collectionName;

        private int saveRemark;

        public Builder(T feginClientClass, MongoTemplate mongoTemplate) {
            this.feginClientClass = feginClientClass;
            this.mongoTemplate = mongoTemplate;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder operateType(String operateType) {
            this.operateType = operateType;
            return this;
        }

        public Builder dataId(Long dataId) {
            this.dataId = dataId;
            return this;
        }

        public Builder enterpriseId(Long enterpriseId) {
            this.enterpriseId = enterpriseId;
            return this;
        }

        public Builder precictId(Long precictId) {
            this.precictId = precictId;
            return this;
        }

        public Builder collectionName(String collectionName) {
            this.collectionName = collectionName;
            return this;
        }

        public Builder saveRemark(int saveRemark) {
            this.saveRemark = saveRemark;
            return this;
        }

        public BusinessLogUtils build() {
            return new BusinessLogUtils<T>(feginClientClass, mongoTemplate, userId, userName, operateType, dataId, enterpriseId, precictId, collectionName, saveRemark);
        }


        /**
         * 比较两个对象字段的变化
         *
         * @param oldBean
         * @param newBean
         * @return
         */
        public String contrastObj(Object oldBean, Object newBean) {
            StringBuilder sb = new StringBuilder();
            try {
                Class clazz = oldBean.getClass();
                List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).filter(t -> t.getAnnotation(LogRecord.class) != null).collect(Collectors.toList());
                for (Field field : fields) {

                    //获取字段的取值
                    Method method = new PropertyDescriptor(field.getName(), clazz).getReadMethod();

                    String oldValue = method.invoke(oldBean) == null ? "" : (Date.class.equals(field.getType()) ?
                            DateUtils.dateToStr((Date) method.invoke(oldBean)) : String.valueOf(method.invoke(oldBean)));

                    String newValue = method.invoke(newBean) == null ? "" : (Date.class.equals(field.getType()) ?
                            DateUtils.dateToStr((Date) method.invoke(newBean)) : String.valueOf(method.invoke(newBean)));

                    if (StringUtils.isBlank(oldValue) && StringUtils.isBlank(newValue)) {
                        continue;
                    }

                    //获取字段注解的属性
                    LogRecord record = field.getAnnotation(LogRecord.class);

                    //如果是字典类型
                    /*String dicCode = record.dicCode();
                    if (StringUtils.isNotBlank(dicCode)) {
                        Map<String, String> dicMap = getDicMap(dicCode);
                        if (dicMap == null) continue;

                        oldValue = dicMap.get(oldValue);
                        newValue = dicMap.get(newValue);
                    }*/

                    //如果是布尔值字段
                    int remark = record.remark();
                    if (remark == 1) {
                        oldValue = "0".equals(oldValue) ? "否" : "是";
                        newValue = "0".equals(newValue) ? "否" : "是";
                    }

                    //需要除法处理
                    String divide = record.divide();
                    if (StringUtils.isNotBlank(divide)) {
                        oldValue = String.valueOf(new BigDecimal(oldValue).divide(new BigDecimal(divide)).setScale(0, BigDecimal.ROUND_DOWN));
                        newValue = String.valueOf(new BigDecimal(newValue).divide(new BigDecimal(divide)).setScale(0, BigDecimal.ROUND_DOWN));
                    }

                    //变化内容拼接
                    String name = record.name();
                    oldValue = StringUtils.isBlank(oldValue) ? "空" : oldValue;
                    newValue = StringUtils.isBlank(newValue) ? "空" : newValue;
                    if (oldValue.equals(newValue)) {
                        continue;
                    }
                    sb.append(name + " ");
                    sb.append("从" + " ");
                    sb.append("[ " + oldValue + " ]" + " 修改为 " + "[ " + newValue + " ]");
                    sb.append(";");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (saveRemark == 0) {
                BizLogEntity bizLogEntity = new BizLogEntity();
                bizLogEntity.setOperateContent(sb.toString());
                saveData(bizLogEntity);
            }

            return sb.toString();
        }


        /**
         * 记录单个日志内容
         *
         * @param operateContent
         */
        public void recordOne(String operateContent) {
            BizLogEntity bizLogEntity = new BizLogEntity();
            bizLogEntity.setOperateContent(operateContent);
            saveData(bizLogEntity);
        }


        /**
         * 批量记录日志内容
         *
         * @param entityList
         */
        public void recordList(List<BizLogEntity> entityList) {
            saveData(entityList);
        }


        /**
         * 获取数据字典
         *
         * @param dicCode
         * @return
         */
        /*public Map<String, String> getDicMap(String dicCode) {
            try {
                Method method = this.feginClientClass.getClass().getMethod(GET_DIC_METHOD, Long.class, Long.class, String.class);
                Map<String, String> dicMap = (Map) method.invoke(this.feginClientClass, enterpriseId, 0L, dicCode);
                AssertUtil.isNull(dicMap, "数据字典为空");
                return dicMap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }*/


        /**
         * 保存日志到Mongdb
         *
         * @param bizLogEntity
         */
        public void saveData(BizLogEntity bizLogEntity) {
            bizLogEntity.setEnterpriseId(enterpriseId);
            bizLogEntity.setPrecictId(precictId);
            bizLogEntity.setUserId(userId);
            bizLogEntity.setUserName(userName);
            bizLogEntity.setOperateDate(new Date());
            bizLogEntity.setDataId(bizLogEntity.getDataId() == null ? dataId : bizLogEntity.getDataId());
            bizLogEntity.setOperateType(StringUtils.isBlank(bizLogEntity.getOperateType()) ? operateType : bizLogEntity.getOperateType());
            this.mongoTemplate.save(bizLogEntity, StringUtils.isBlank(collectionName) ? DEFAULT_COLLECTION_NAME : collectionName);
        }


        /**
         * 保存日志到Mongdb
         *
         * @param entityList
         */
        public void saveData(List<BizLogEntity> entityList) {
            entityList.stream().forEach(bizLogEntity -> {
                bizLogEntity.setEnterpriseId(enterpriseId);
                bizLogEntity.setPrecictId(precictId);
                bizLogEntity.setUserId(userId);
                bizLogEntity.setUserName(userName);
                bizLogEntity.setOperateDate(new Date());
                bizLogEntity.setDataId(bizLogEntity.getDataId() == null ? dataId : bizLogEntity.getDataId());
                bizLogEntity.setOperateType(StringUtils.isBlank(bizLogEntity.getOperateType()) ? operateType : bizLogEntity.getOperateType());
                this.mongoTemplate.save(bizLogEntity, StringUtils.isBlank(collectionName) ? DEFAULT_COLLECTION_NAME : collectionName);
            });
        }
    }


    public static void main(String[] args) {
        AppUser oldAppUser = new AppUser();
        oldAppUser.setUserName("张三");
        oldAppUser.setEnterpriseName("软件园");
        oldAppUser.setLastLoginTime(DateUtils.strToDate("2021-02-08"));
        oldAppUser.setIsActive(0);
        oldAppUser.setAge(100);

        AppUser newAppUser = new AppUser();
        newAppUser.setUserName("李四");
        newAppUser.setEnterpriseName("阿里巴巴");
        newAppUser.setLastLoginTime(DateUtils.strToDate("2021-03-03"));
        newAppUser.setIsActive(1);
        newAppUser.setAge(120);

        String content = new Builder(null, null)
                .enterpriseId(974L)
                .userName("admin")
                .saveRemark(1)
                .contrastObj(oldAppUser, newAppUser);
        System.out.println(content);
    }

}
