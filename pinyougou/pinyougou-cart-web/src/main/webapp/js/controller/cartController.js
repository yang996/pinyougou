app.controller("cartController",function ($scope,cartService) {

    //获取用户名
    $scope.getUsername=function () {
        cartService.getUsername().success(function (response) {
            $scope.username=response.username;
        });
    };

    //刚进入页面的时候获取购物车列表数据
    $scope.findCartList=function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList=response;
            //计算购买总数和总价
            $scope.totalValue=cartService.sumTotalValue(response);
        });
    };

    //用户添加或删除商品
    $scope.addItemToCartList=function (itemId, num) {
        cartService.addItemToCartList(itemId,num).success(function (resopnse) {
            if (resopnse.success){
                $scope.findCartList();
            }else {
                alert(resopnse.message);
            }
        })
    }
});