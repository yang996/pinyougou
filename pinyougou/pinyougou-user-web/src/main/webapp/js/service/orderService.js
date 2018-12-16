app.service("orderService",function ($http) {

    //查询订单
    this.search=function (searchMap) {
      return $http.post("order/search.do",searchMap);
    };

    //取消订单
    this.cancelOrder=function (orderId) {
      return $http.get("order/cancelOrder.do?orderId="+orderId);
    };
});