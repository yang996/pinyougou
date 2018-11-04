app.controller("searchController",function ($scope,$location, searchService) {
    $scope.search=function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap=response;
            //构建页面分页导航条信息
            buildPageInfo();
        });
    };

    //创建搜索对象
    $scope.searchMap={"keywords":"", "category":"",
        "brand":"", "spec":{}, "price":"","pageNo":1, "pageSize":20, "sortField":"", "sort":""};

    //添加过滤条件
    $scope.addSearchItem=function (key, value) {
        if("brand"==key || "category"==key || "price"==key){
            //如果点击的是品牌或者分类
            $scope.searchMap[key]=value;
        }else {
            //点击的是规格
            $scope.searchMap.spec[key]=value;
        }
        //添加过滤条件,默认查询第一页
        $scope.searchMap.pageNo=1;
        //点击过滤条件后需要重新搜索
        $scope.search();
    };

    //删除过滤条件
    $scope.removeSearchItem=function (key) {
        if("brand"==key || "category"==key || "price"==key){
            //如果点击的是品牌或者分类
            $scope.searchMap[key]="";
        }else {
            //点击的是规格
            delete $scope.searchMap.spec[key];
        }
        //删除过滤条件,默认查询第一页
        $scope.searchMap.pageNo=1;
        //点击过滤条件后需要重新搜索
        $scope.search();
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

    //排序搜索
    $scope.sortSearch=function (sortField, sort) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    };

    //获取门户首页搜索关键字  $location.search()得到的是json对象
    $scope.loadKeywords=function () {
        $scope.searchMap.keywords=$location.search()["keywords"];
        $scope.search();
    }

});

