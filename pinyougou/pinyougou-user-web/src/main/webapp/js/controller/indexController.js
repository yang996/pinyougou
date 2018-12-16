app.controller("indexController", function ($scope, userService,orderService) {

    //获取用户名
    $scope.getUsername = function () {
        userService.getUsername().success(function (response) {
            $scope.username = response.username;
        });
    };

    //获取商品对应的规格
    $scope.JsonToString=function (item,specMap) {
        var spec=JSON.parse(specMap[item.id]);
        var str="";
        for (var key in spec) {
            str+=key+":"+spec[key]+" ";
        }
        return str;
    };

    //计算订单总价
    $scope.totalMoney=function (itemList) {
        var totalMoney=0.0;
        for (var i = 0; i < itemList.length; i++) {
            var item = itemList[i];
            totalMoney+=item.totalFee * item.num;
        }
        return totalMoney;
    };

    //分页参数
    $scope.searchMap={"pageNo":1, "pageSize":5};


    //获取订单列表
    $scope.search=function () {
        orderService.search($scope.searchMap).success(function (response) {
            $scope.resultMap=response;
            //构建页面分页导航条信息
            buildPageInfo();
        });
    };

    //商品操作
    $scope.operation=function (order) {
        if (order.status!=1){
            return true;
        }
        return false;
    };

    //已发货
    $scope.shipped=function (order) {
        if (order.status==4){
            return true;
        }
        return false;
    };

    //未付款
    $scope.nopayment=function (order) {
        if (order.status==1){
            return true;
        }
        return false;
    };

    //未发货
    $scope.unshipped=function (order) {
        if (order.status==3 || order.status==2){
            return true;
        }
        return false;
    };

    //取消订单
    $scope.cancelOrder=function (orderId) {
        alert("是否确定取消订单");
        orderService.cancelOrder(orderId).success(function (response) {
            if (response.success){
                alert(response.message);
                location.reload();
            }else {
                alert(response.message);
            }
        });
    };

    //判断是否为当前页
    $scope.isCurrentPage=function (pageNo) {
        return $scope.searchMap.pageNo == pageNo;
    };

    //根据页号查询
    $scope.queryByPageNo=function (pageNo) {
        pageNo=parseInt(pageNo);
        if (0<pageNo && pageNo<=$scope.resultMap.totalPages){
            $scope.searchMap.pageNo=pageNo;
            $scope.search();
        }
    };

    //构建页面分页导航条信息
    buildPageInfo=function () {
        //定义要在页面显示的页号的集合
        $scope.pageNoList=[];

        //定义要在页面显示的页号的数量
        var showPageNoTotal=5;

        //起始页号
        var startPageNo=1;

        //结束页号
        var endPageNo=$scope.resultMap.totalPages;

        //如果总页数大于要显示的页数才有需要处理显示页号数,否则直接显示所有页号
        if($scope.resultMap.totalPages>showPageNoTotal){
            //计算当前页左右间隔页数
            var interval=Math.floor(showPageNoTotal/2);
            //根据间隔得出起始,结束页号
            startPageNo=parseInt($scope.searchMap.pageNo)-interval;
            endPageNo=parseInt($scope.searchMap.pageNo)+interval;
            //处理页号越界
            if (startPageNo>0){
                if (endPageNo>$scope.resultMap.totalPages){
                    startPageNo=startPageNo-(endPageNo-$scope.resultMap.totalPages);
                    endPageNo=$scope.resultMap.totalPages;
                }
            }else {
                endPageNo=endPageNo-(startPageNo-1);
                startPageNo=1;
            }
        }

        //分页导航条上的前,后那三个点
        $scope.frontDot=false;
        $scope.backDot=false;

        if (1<startPageNo){
            $scope.frontDot=true;
        }

        if (endPageNo<$scope.resultMap.totalPages){
            $scope.backDot=true;
        }

        //设置要显示的页号
        for (var i=startPageNo;i<=endPageNo;i++){
            $scope.pageNoList.push(i);
        }
    };

});