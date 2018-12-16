app.service("cartService",function ($http) {
    //获取用户名
    this.getUsername=function () {
        return $http.get("/cart/getUsername.do?t="+Math.random());
    };

    //获取购物车列表
    this.findCartList=function () {
        return $http.get("cart/findCartList.do?t="+Math.random());
    };

    //添加商品进购物车
    this.addItemToCartList=function (itemId, num) {
        return $http.get("cart/addItemToCartList.do?itemId="+itemId+"&num="+num+"&t="+Math.random());
    };

    this.sumTotalValue=function (cartList) {
        var totalValue={"totalNum":0,"totalMoney":0.0};
        for (var i = 0; i < cartList.length; i++) {
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++) {
                var orderItem = cart.orderItemList[j];
                totalValue.totalNum+=orderItem.num;
                totalValue.totalMoney+=orderItem.totalFee;
            }
        }
        return totalValue;
    };

    //提交订单
    this.submitOrder=function (order) {
        return $http.post("order/add.do",order);
    };
});
