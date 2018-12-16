app.service("addressService",function ($http) {

    this.getAddressList=function () {
        return $http.get("address/findAddressList.do");
    };

    this.addAddress=function (entity) {
        return $http.post("address/addAddress.do",entity);
    }
});