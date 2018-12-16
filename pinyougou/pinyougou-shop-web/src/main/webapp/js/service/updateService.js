app.service("updateService",function ($http) {
    //用户修改密码
    this.updatePassword=function (entity) {
        return $http.post("../seller/updatePassword.do",entity);
    };
});