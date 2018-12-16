//定义业务服务
app.service("seckillGoodsService",function ($http) {

    this.getUsername=function () {
        return $http.get("seckillOrder/getUsername.do");
    };

    this.findList = function () {
        return $http.get("../seckillGoods/findList.do");
    };

    this.findOne = function (id) {
        return $http.get("../seckillGoods/findOne.do?id=" + id);
    };

    this.submitOrder = function (id) {
        return $http.get("../seckillOrder/submitOrder.do?seckillId=" + id);
    };
});