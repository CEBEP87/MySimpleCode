package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.impl;

import com.mongodb.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Activity;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.repository.ActivityRepository;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.ActivityService;

import java.util.List;

/**
 * Activity Service
 *
 * @author Samsonov_KY
 * @since 21.09.2017
 */
@Service
public class ActivityServiceImpl implements ActivityService {

    /**
     * Logger object
     */
    private Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    /**
     * Activity Repository
     */
    @Autowired
    private ActivityRepository activityRepository;


    /**
     * Data-library
     */
    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * Initiating a data source host
     */
    @Value("${spring.data.mongodb.host}")
    private String host;
    /**
     * Initiating database
     */
    @Value("${spring.data.mongodb.database}")
    private String database;

    @Override
    public List<DBObject> findAll() {
        Bson nativeQuery =  new Document("yo","db.Activity.find({});");
        Mongo mongo = new Mongo(host);
        //MongoClient mongoClient = new MongoClient(host);
        DB db=mongo.getDB(database);
        DBCollection collection=db.getCollection("Activity");
        DBObject doc = collection.findOne();
        DBCursor cursor = collection.find();
        List<DBObject> myList = cursor.toArray();
        return myList;
    }

    @Override
    public List<DBObject> findAllNative() throws Exception {
        Mongo mongo = new Mongo(host);
        DB db = mongo.getDB(database);
        DBCollection collection = db.getCollection("Activity");
        DBCursor cursor = collection.find();
        List<DBObject> myList = cursor.toArray();
        return myList;
    }

    @Override
    public Activity findOneById(String id) {
        return activityRepository.findOne(id);
    }

    @Override
    public Activity findOneByPosition(String position) {
        return activityRepository.findOneByIdPosition(position);
    }

    @Override
    public Activity postActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public Activity putActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    public void deleteActivity(String id) {
        activityRepository.delete(id);
    }

    @Override
    public ResponseEntity getActivitysService(String id, String position) {
        try {
            if (id != null) return new ResponseEntity(findOneById(id), HttpStatus.OK);
            else if (position != null) return new ResponseEntity(findOneByPosition(position), HttpStatus.OK);
            return new ResponseEntity(findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity("Error inner Server", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
