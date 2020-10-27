package cyx.secondary.mall.util;

import java.util.LinkedHashMap;
import java.util.Map;

//分页查询参数
public class PageQueryUserUtil extends LinkedHashMap<String, Object> {

    //当前页码
    private int page;

    //每页条数
    private int limit;

    //用户id
//    private long uid;

    public PageQueryUserUtil(Map<String, Object> params) {
        this.putAll(params);

        //分页查询参数
        this.page = Integer.parseInt(params.get("page").toString());
        this.limit = Integer.parseInt(params.get("limit").toString());
//        this.uid = Integer.parseInt(params.get("uid").toString());
        this.put("start", (page - 1) * limit);
        this.put("page", page);
        this.put("limit", limit);
//        this.put("uid",uid);
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

//    public long getUid() {
//        return uid;
//    }
//
//    public void setUid(long uid) {
//        this.uid = uid;
//    }

    @Override
    public String toString() {
//        return "PageUtil{" +
//                "page=" + page +
//                ", limit=" + limit +
//                ", uid=" + uid +
//                '}';

        return "PageUtil{" +
                "page=" + page +
                ", limit=" + limit +
                '}';
    }
}
