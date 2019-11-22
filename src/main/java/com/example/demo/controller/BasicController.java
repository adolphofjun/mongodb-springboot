package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.bo.UserBO;
import com.mongodb.DB;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.reactivestreams.client.internal.GridFSBucketImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("test")
public class BasicController {
    private static Logger logger = LoggerFactory.getLogger(BasicController.class);

    @Autowired private MongoTemplate mongoTemplate;
    @Autowired private GridFsOperations gridFsOperations;
    @Autowired private GridFsTemplate gridFsTemplate;
    @Autowired private GridFSBucket gridFSBucket;




    private static final  String collectionName = "test";

    @ApiOperation(value = "保存测试")
    @PostMapping("/save")
    public void insert(UserBO userBO){
        logger.info("==============" + JSON.toJSONString(userBO));
        mongoTemplate.insert(userBO);
    }


    @ApiOperation(value = "修改测试")
    @PostMapping("/edit")
    public UpdateResult edit(UserBO userBO){
        logger.info("==============" + JSON.toJSONString(userBO));
        Query query=new Query(Criteria.where("name").is(userBO.getName()));
        return mongoTemplate.updateFirst(query,new Update().set("description",userBO.getDescription()),UserBO.class);
    }


    @ApiOperation(value = "查询测试")
    @PostMapping("/search/{name}")
    public List<UserBO> search(@PathVariable String name){
        logger.info("==============search");
        Query query=new Query(Criteria.where("name").is(name));
        List<UserBO> user =  mongoTemplate.find(query, UserBO.class);
        return user;

    }


    @ApiOperation(value = "删除测试")
    @PostMapping("/delete/{name}")
    public DeleteResult delete(@PathVariable String name){
        logger.info("==============search");
        Query query=new Query(Criteria.where("name").is(name));
        return mongoTemplate.remove(query, UserBO.class);
    }


    @ApiOperation(value = "文件上传测试")
    @PostMapping("/upload")
    public ObjectId fileUdload(MultipartFile file){
        ObjectId objectId=null;
        System.out.println(file.getContentType());
        System.out.println(file.getName());
        System.out.println(file.getOriginalFilename());

        try{
            return gridFsTemplate.store(file.getInputStream(),file.getOriginalFilename(),".txt");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


//    @ApiOperation(value = "文件下载测试")
//    @PostMapping("/download/{name}")
//    public ObjectId fileDownload(@PathVariable String name, HttpServletResponse response){
//        try{
//            GridFSFile file = gridFsTemplate.findOne(query(whereFilename().is(name)));
//            if(file != null){
//                System.out.println("_id:"+file.getId());
//                System.out.println("_objectId:"+file.getObjectId());
//                GridFSDownloadStream in = gridFSBucket.openDownloadStream(file.getObjectId());
//
//                GridFsResource resource = new GridFsResource(file,in);
//                InputStream inputStream = resource.getInputStream();
//                byte[] f = getBytes(inputStream);
//                OutputStream out = response.getOutputStream();
//                out.write(f);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }


//    @RequestMapping(value = "/downloadFile")
//    public void downloadFile(@RequestParam(name = "file_id") String fileId, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        Query query = Query.query(Criteria.where("_id").is(fileId));
//        // 查询单个文件
//        GridFSFile gfsfile = gridFsTemplate.findOne(query);
//        if (gfsfile == null) {
//            return;
//        }
//        String fileName = gfsfile.getFilename().replace(",", "");
//        //处理中文文件名乱码
//        if (request.getHeader("User-Agent").toUpperCase().contains("MSIE") ||
//                request.getHeader("User-Agent").toUpperCase().contains("TRIDENT")
//                || request.getHeader("User-Agent").toUpperCase().contains("EDGE")) {
//            fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
//        } else {
//            //非IE浏览器的处理：
//            fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
//        }
//        // 通知浏览器进行文件下载
//        response.setContentType(gfsfile.getContentType());
//        response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
////        gfsfile.writeTo(response.getOutputStream());
//
//    }




    @ApiOperation(value = "下载文件测试", notes = "下载文件测试demo")
    @RequestMapping(value = "/download", method = {RequestMethod.GET, RequestMethod.POST})
    public void downloadFile(@RequestParam(name = "file_id") String fileId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("准备下载文件....");
        Query query = Query.query(Criteria.where("_id").is(fileId));
        // 查询单个文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(query);
        if (gridFSFile == null) {
            return;
        }

        String fileName = gridFSFile.getFilename().replace(",", "");
        String contentType = gridFSFile.getMetadata().get("_contentType").toString();

        // 通知浏览器进行文件下载
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");
        GridFSDownloadStream gridFSDownloadStream = (GridFSDownloadStream) gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        GridFsResource resource = new GridFsResource(gridFSFile, gridFSDownloadStream);

        OutputStream outputStream = response.getOutputStream();
        InputStream inputStream = resource.getInputStream();
        IOUtils.copy(inputStream, outputStream);
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

}
