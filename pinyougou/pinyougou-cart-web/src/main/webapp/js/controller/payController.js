app.controller("payController",function ($location,$scope,payService,cartService) {

    $scope.getUsername=function () {
        cartService.getUsername().success(function (response) {
            $scope.username=response.username;
        });
    };

    //生成二维码
    $scope.createNative=function () {
        //支付日志id(交易号)
       $scope.outTradeNo=$location.search()["outTradeNo"];
        payService.createNative($scope.outTradeNo).success(function (response) {
            if ("SUCCESS"==response.result_code){
               //计算总金额
                $scope.money=(response.totalFee/100).toFixed(2);
                //生成二维码
                var qr=new QRious({
                    element:document.getElementById("qrious"),
                    size:250,
                    level:"M",
                    value:response.code_url
                });

                //查询支付状态
                queryPayStatus($scope.outTradeNo);
            }else {
                alert("生成二维码失败");
            }
        });
    };

    //查询支付状态
    queryPayStatus=function (outTradeNo) {
        payService.queryPayStatus(outTradeNo).success(function (response) {
            //支付成功
            if (response.success){
                location.href="paysuccess.html#?money="+$scope.money;
            }else {
                if ("二维码重新生成"==response.message){
                    //重新生成二维码
                    $scope.createNative();
                }
                //支付失败
                location.href="payfail.html";
            }
        });
    };

    //获取总金额
    $scope.getMoney=function () {
        $scope.money=$location.search()["money"];
    };
});