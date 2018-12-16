app.controller("orderInfoController",function ($scope,cartService,addressService) {

    $scope.addAddress=function () {
        addressService.addAddress($scope.entity).success(function (response) {
            if (response.success){
                location.href="getOrderInfo.html";
            }else {
                alert(response.message);
            }
        });
    };

    //获取用户名
    $scope.getUsername=function(){
        cartService.getUsername().success(function (response) {
            $scope.username=response.username;
        });
    };

    //获取用户收获地址列表
    $scope.getAddressList=function () {
        addressService.getAddressList().success(function (response) {
            $scope.addressList=response;

            //默认地址
            for (var i = 0; i < $scope.addressList.length; i++) {
                var address = $scope.addressList[i];
                if (address.isDefault=="1"){
                    $scope.address=address;
                    break;
                }
            }
        })
    };

    //用户选择收获地址
    $scope.selectedAddress=function (address) {
        $scope.address=address;
    };

    //判断用户是否选择了地址
    $scope.isAddressSelected=function (address) {
        if($scope.address==address){
            return true;
        }
        return false;
    };

    //获取送货清单列表
    $scope.getCartList=function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList=response;
            //计算购买总数和总价
            $scope.totalValue=cartService.sumTotalValue(response);
        });
    };

    //订单
    $scope.order={"paymentType":"1"};
    //选择支付类型
    $scope.selectPayType=function (type) {
      $scope.order.paymentType=type;
    };

    //提交订单
    $scope.submitOrder=function () {
        //用户收货地址
        $scope.order.receiverAreaName=$scope.address.address;
        $scope.order.receiverMobile=$scope.address.mobile;
        $scope.order.receiver=$scope.address.contact;
        cartService.submitOrder($scope.order).success(function (response) {
            if(response.success){
                if ($scope.order.paymentType=="1"){
                    //微信支付
                    // 携带支付业务 id ，跳转到支付页面
                    location.href = "pay.html#?outTradeNo=" + response.message;
                }else {
                    //货到付款
                    location.href="paysuccess.html";
                }
            }else {
                alert(response.message);
            }
        });
    };
});