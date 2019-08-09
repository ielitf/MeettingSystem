package com.hskj.meettingsys.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.hskj.meettingsys.bean.MqttMeetingListBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MQTT_MEETING_LIST_BEAN".
*/
public class MqttMeetingListBeanDao extends AbstractDao<MqttMeetingListBean, Long> {

    public static final String TABLENAME = "MQTT_MEETING_LIST_BEAN";

    /**
     * Properties of entity MqttMeetingListBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property NewsId = new Property(0, Long.class, "newsId", true, "_id");
        public final static Property Id = new Property(1, int.class, "id", false, "ID");
        public final static Property RoomNum = new Property(2, String.class, "roomNum", false, "ROOM_NUM");
        public final static Property RoomName = new Property(3, String.class, "roomName", false, "ROOM_NAME");
        public final static Property Name = new Property(4, String.class, "name", false, "NAME");
        public final static Property IsOpen = new Property(5, String.class, "isOpen", false, "IS_OPEN");
        public final static Property EndDate = new Property(6, long.class, "endDate", false, "END_DATE");
        public final static Property StartDate = new Property(7, long.class, "startDate", false, "START_DATE");
        public final static Property TemplateId = new Property(8, int.class, "templateId", false, "TEMPLATE_ID");
        public final static Property BookPerson = new Property(9, String.class, "bookPerson", false, "BOOK_PERSON");
        public final static Property Sign = new Property(10, String.class, "sign", false, "SIGN");
    }


    public MqttMeetingListBeanDao(DaoConfig config) {
        super(config);
    }
    
    public MqttMeetingListBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MQTT_MEETING_LIST_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: newsId
                "\"ID\" INTEGER NOT NULL ," + // 1: id
                "\"ROOM_NUM\" TEXT," + // 2: roomNum
                "\"ROOM_NAME\" TEXT," + // 3: roomName
                "\"NAME\" TEXT," + // 4: name
                "\"IS_OPEN\" TEXT," + // 5: isOpen
                "\"END_DATE\" INTEGER NOT NULL ," + // 6: endDate
                "\"START_DATE\" INTEGER NOT NULL ," + // 7: startDate
                "\"TEMPLATE_ID\" INTEGER NOT NULL ," + // 8: templateId
                "\"BOOK_PERSON\" TEXT," + // 9: bookPerson
                "\"SIGN\" TEXT);"); // 10: sign
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_MQTT_MEETING_LIST_BEAN_ID ON \"MQTT_MEETING_LIST_BEAN\"" +
                " (\"ID\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MQTT_MEETING_LIST_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MqttMeetingListBean entity) {
        stmt.clearBindings();
 
        Long newsId = entity.getNewsId();
        if (newsId != null) {
            stmt.bindLong(1, newsId);
        }
        stmt.bindLong(2, entity.getId());
 
        String roomNum = entity.getRoomNum();
        if (roomNum != null) {
            stmt.bindString(3, roomNum);
        }
 
        String roomName = entity.getRoomName();
        if (roomName != null) {
            stmt.bindString(4, roomName);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String isOpen = entity.getIsOpen();
        if (isOpen != null) {
            stmt.bindString(6, isOpen);
        }
        stmt.bindLong(7, entity.getEndDate());
        stmt.bindLong(8, entity.getStartDate());
        stmt.bindLong(9, entity.getTemplateId());
 
        String bookPerson = entity.getBookPerson();
        if (bookPerson != null) {
            stmt.bindString(10, bookPerson);
        }
 
        String sign = entity.getSign();
        if (sign != null) {
            stmt.bindString(11, sign);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MqttMeetingListBean entity) {
        stmt.clearBindings();
 
        Long newsId = entity.getNewsId();
        if (newsId != null) {
            stmt.bindLong(1, newsId);
        }
        stmt.bindLong(2, entity.getId());
 
        String roomNum = entity.getRoomNum();
        if (roomNum != null) {
            stmt.bindString(3, roomNum);
        }
 
        String roomName = entity.getRoomName();
        if (roomName != null) {
            stmt.bindString(4, roomName);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String isOpen = entity.getIsOpen();
        if (isOpen != null) {
            stmt.bindString(6, isOpen);
        }
        stmt.bindLong(7, entity.getEndDate());
        stmt.bindLong(8, entity.getStartDate());
        stmt.bindLong(9, entity.getTemplateId());
 
        String bookPerson = entity.getBookPerson();
        if (bookPerson != null) {
            stmt.bindString(10, bookPerson);
        }
 
        String sign = entity.getSign();
        if (sign != null) {
            stmt.bindString(11, sign);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public MqttMeetingListBean readEntity(Cursor cursor, int offset) {
        MqttMeetingListBean entity = new MqttMeetingListBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // newsId
            cursor.getInt(offset + 1), // id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // roomNum
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // roomName
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // isOpen
            cursor.getLong(offset + 6), // endDate
            cursor.getLong(offset + 7), // startDate
            cursor.getInt(offset + 8), // templateId
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // bookPerson
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10) // sign
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MqttMeetingListBean entity, int offset) {
        entity.setNewsId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setId(cursor.getInt(offset + 1));
        entity.setRoomNum(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setRoomName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setIsOpen(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setEndDate(cursor.getLong(offset + 6));
        entity.setStartDate(cursor.getLong(offset + 7));
        entity.setTemplateId(cursor.getInt(offset + 8));
        entity.setBookPerson(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setSign(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MqttMeetingListBean entity, long rowId) {
        entity.setNewsId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MqttMeetingListBean entity) {
        if(entity != null) {
            return entity.getNewsId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MqttMeetingListBean entity) {
        return entity.getNewsId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}