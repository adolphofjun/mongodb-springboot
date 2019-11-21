package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.bo.UserBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("test")
public class BasicController {
    private static Logger logger = LoggerFactory.getLogger(BasicController.class);

    @Autowired private MongoTemplate mongoTemplate;


    private static final  String collectionName = "test";

    @ApiOperation(value = "保存测试")
    @PostMapping("/save")
    public void insert(UserBO userBO){
        logger.info("==============" + JSON.toJSONString(userBO));
        mongoTemplate.insert(userBO);
    }


    @ApiOperation(value = "查询测试")
    @PostMapping("/search")
    public List<UserBO> search(){
        logger.info("==============search");
        Query query=new Query(Criteria.where("name").is("qqq"));
        List<UserBO> user =  mongoTemplate.find(query, UserBO.class);
        return user;

    }


}
