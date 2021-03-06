package com.demo.client;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class HbaseClient {
    private static Admin admin;
    private static Connection conn;

    static {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.rootdir", "hdfs://192.168.0.100:9000/hbase");
        conf.set("hbase.zookeeper.quorum", "192.168.0.100");
        conf.set("hbase.client.scanner.timeout.period", "50000");
        conf.set("hbase.rpc.timeout", "50000");
        try {
            conn = ConnectionFactory.createConnection(conf);
            admin = conn.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createTable(String tableName, String... columnFamilies) throws IOException {
        TableName tablename = TableName.valueOf(tableName);
        if(admin.tableExists(tablename)){
            System.out.println("Table Exists");
        }else{
        System.out.println("Start create table");
        HTableDescriptor tableDescriptor = new HTableDescriptor(tablename);
        for (String columnFamliy : columnFamilies) {
            HTableDescriptor column = tableDescriptor.addFamily(new HColumnDescriptor(columnFamliy));
        }
        admin.createTable(tableDescriptor);
        System.out.println("Create Table success");
        }
    }


    public static String getData(String tableName, String rowKey, String famliyName, String column) throws IOException {
        Table table = conn.getTable(TableName.valueOf(tableName));
        byte[] row = Bytes.toBytes(rowKey);
        Get get = new Get(row);
        Result result = table.get(get);
        byte[] resultValue = result.getValue(famliyName.getBytes(), column.getBytes());
        if (null == resultValue){
            return null;
        }
        return new String(resultValue);
    }
    public static void putData(String tablename, String rowkey, String famliyname,String colum,String data) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tablename));
        Put put = new Put(rowkey.getBytes());
        put.addColumn(famliyname.getBytes(),colum.getBytes(),data.getBytes());
        table.put(put);
    }
}
