app.service("orderService",function ($http) {

    this.search = function (page, rows, searchEntity) {
        return $http.post("../order/findOrderList.do?page=" + page + "&rows=" + rows, searchEntity);
    };
});