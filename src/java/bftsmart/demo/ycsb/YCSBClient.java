/**
 * Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and the authors indicated in the @author tags
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bftsmart.demo.ycsb;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import bftsmart.tom.ServiceProxy;

import site.ycsb.ByteIterator;
import site.ycsb.DB;
import site.ycsb.DBException;
import site.ycsb.Status;

/**
 *
 * @author Marcel Santos
 *
 */
public class YCSBClient extends DB {

    private static AtomicInteger counter = new AtomicInteger();
    private ServiceProxy proxy = null;
    private int myId;

    public void init() throws DBException {
        Properties props = getProperties();
        int initId = Integer.valueOf((String) props.get("smart-initkey"));
        myId = initId + counter.addAndGet(1);
        proxy = new ServiceProxy(myId);
        System.out.println("YCSBKVClient. Initiated client id: " + myId);
    }

    public Status delete(String arg0, String arg1) {
        throw new UnsupportedOperationException();
    }

    public Status insert(String table, String key,
            HashMap<String, ByteIterator> values) {

        Iterator<String> keys = values.keySet().iterator();
        HashMap<String, byte[]> map = new HashMap<>();
        while (keys.hasNext()) {
            String field = keys.next();
            map.put(field, values.get(field).toArray());
        }
        YCSBMessage msg = YCSBMessage.newInsertRequest(table, key, map);
        byte[] reply = proxy.invokeOrdered(msg.getBytes());
        YCSBMessage replyMsg = YCSBMessage.getObject(reply);
        return new Status(String.valueOf(replyMsg.getResult()), replyMsg.getErrorMsg());
    }

    @Override
    public Status read(String table, String key, Set<String> fields, Map<String, ByteIterator> result) {
        HashMap<String, byte[]> results = new HashMap<>();
        YCSBMessage request = YCSBMessage.newReadRequest(table, key, fields, results);
        byte[] reply = proxy.invokeUnordered(request.getBytes());
        YCSBMessage replyMsg = YCSBMessage.getObject(reply);
        assert replyMsg != null;
        return new Status(String.valueOf(replyMsg.getResult()), replyMsg.getErrorMsg());
    }
    
    public Status scan(String arg0, String arg1, int arg2, Set<String> arg3,
            Vector<HashMap<String, ByteIterator>> arg4) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Status update(String table, String key, Map<String, ByteIterator> values) {
        return null;
    }

    @Override
    public Status insert(String table, String key, Map<String, ByteIterator> values) {
        return null;
    }

    public int update(String table, String key,
            HashMap<String, ByteIterator> values) {
        Iterator<String> keys = values.keySet().iterator();
        HashMap<String, byte[]> map = new HashMap<>();
        while (keys.hasNext()) {
            String field = keys.next();
            map.put(field, values.get(field).toArray());
        }
        YCSBMessage msg = YCSBMessage.newUpdateRequest(table, key, map);
        byte[] reply = proxy.invokeOrdered(msg.getBytes());
        YCSBMessage replyMsg = YCSBMessage.getObject(reply);
        return replyMsg.getResult();
    }

}
