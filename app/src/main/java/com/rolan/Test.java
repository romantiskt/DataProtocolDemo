package com.rolan;

import java.util.List;

/**
 * Created by wangyang on 2018/4/24.下午6:55
 */
public class Test {

    private List<PeoplesBean> peoples;

    public List<PeoplesBean> getPeoples() {
        return peoples;
    }

    public void setPeoples(List<PeoplesBean> peoples) {
        this.peoples = peoples;
    }

    public static class PeoplesBean {
        /**
         * id : 0
         * index : 0
         * guid : guid0
         * name : name:0
         * gender : 男
         * company : 加油宝：0
         * email : sd@.com
         * friends : [{"id":23434,"name":"张三"},null,{"id":23434,"name":"张三"},{"id":23434,"name":"张三"}]
         */

        private int id;
        private int index;
        private String guid;
        private String name;
        private String gender;
        private String company;
        private String email;
        private List<FriendsBean> friends;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getGuid() {
            return guid;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public List<FriendsBean> getFriends() {
            return friends;
        }

        public void setFriends(List<FriendsBean> friends) {
            this.friends = friends;
        }

        public static class FriendsBean {
            /**
             * id : 23434
             * name : 张三
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
