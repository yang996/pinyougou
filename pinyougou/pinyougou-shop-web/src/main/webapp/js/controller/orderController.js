app.controller("orderController",function ($scope,$controller,orderService) {

    //加载baseController控制器并传入1个作用域，与angularJs运行时作用域相同.
    $controller("baseController",{$scope:$scope});

    //订单的状态
    $scope.orderStatus = ["","未付款","已付款","未发货","已发货","交易成功","交易关闭","待评价"];

    //支付类型
    $scope.paymentType=["在线支付","货到付款"];

    //加载订单信息
    $scope.searchEntity = {};//初始为空
    $scope.search = function (page, rows) {
        orderService.search(page, rows, $scope.searchEntity).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    };
});