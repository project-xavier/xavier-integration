package org.jboss.xavier.integrations.rbac;

import java.util.List;

/**
 * Represents the Http response body of the RBAC server when '/access' endpoint is invoked. E.g.
 * {
 *   "meta": {
 *     "count": 1,
 *     "limit": 10,
 *     "offset": 0
 *   },
 *   "links": {
 *     "first": "/api/rbac/v1/access/?application=migration-analytics&limit=10&offset=0",
 *     "next": null,
 *     "previous": null,
 *     "last": "/api/rbac/v1/access/?application=migration-analytics&limit=10&offset=0"
 *   },
 *   "data": [
 *     {
 *       "permission": "migration-analytics:*:*",
 *       "resourceDefinitions": []
 *     }
 *   ]
 * }
 */
public class RbacResponse {
    private Meta meta;
    private Links links;
    private List<Acl> data;

    public RbacResponse() {
    }

    public RbacResponse(Meta meta, Links links, List<Acl> data) {
        this.meta = meta;
        this.links = links;
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public List<Acl> getData() {
        return data;
    }

    public void setData(List<Acl> data) {
        this.data = data;
    }

    public static class Meta {
        private Integer count;
        private Integer limit;
        private Integer offset;

        public Meta() {
        }

        public Meta(Integer count, Integer limit, Integer offset) {
            this.count = count;
            this.limit = limit;
            this.offset = offset;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }

        public Integer getOffset() {
            return offset;
        }

        public void setOffset(Integer offset) {
            this.offset = offset;
        }
    }

    public static class Links {
        private String first;
        private String next;
        private String previous;
        private String last;

        public Links() {
        }

        public Links(String first, String next, String previous, String last) {
            this.first = first;
            this.next = next;
            this.previous = previous;
            this.last = last;
        }

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        public String getPrevious() {
            return previous;
        }

        public void setPrevious(String previous) {
            this.previous = previous;
        }

        public String getLast() {
            return last;
        }

        public void setLast(String last) {
            this.last = last;
        }
    }

}
