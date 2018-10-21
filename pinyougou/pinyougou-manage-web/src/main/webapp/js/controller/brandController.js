//定义处理器(动作)
app.controller("brandController", function ($scope, $controller, brandService) {

    //继承baseController
    $controller("baseController",{$scope:$scope});

    // 保存信息,可新增和修改
    $scope.save = function () {
        var obj;
        if ($scope.entity.id != null) {
            obj = brandService.update($scope.entity);
        } else {
            obj = brandService.add($scope.entity);
        }
        obj.success(function (response) {
            if (response.success) {
                //重新加载列表
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });
    };

    //根据主键查询
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        });
    };

    //批量删除信息
    $scope.delete = function () {
        if ($scope.selectedIds.length < 1) {
            alert("请先选中要删除的记录");
            return;
        }
        if (confirm("是否确定删除?")) {
            brandService.delete($scope.selectedIds).success(function (response) {
                if (response.success) {
                    $scope.reloadList();
                    $scope.selectedIds = [];
                } else {
                    alert(response.message);
                }
            });
        }
    };


    //初始化查询对象；为了避免后台接收时候出现解析错误
    $scope.searchEntity = {};
    //多条件查询
    $scope.search = function (page, rows) {
        brandService.search(page, rows, $scope.searchEntity).success(function (response) {
            //更新记录列表
            $scope.list = response.rows;
            //更新总记录数
            $scope.paginationConf.totalItems = response.total;
        });
    };

});