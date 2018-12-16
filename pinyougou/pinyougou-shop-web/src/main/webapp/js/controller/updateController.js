app.controller("updateController", function ($scope, updateService) {


    $scope.entity={"password":"","oldPassword":""};
    //用户修改密码
    $scope.save = function () {
        if ($scope.entity.oldPassword == "" || $scope.entity.password == "") {
            alert("密码不能为空");
            return;
        }
        if ($scope.entity.password != $scope.newPassword) {
            alert("两次输入的新密码不一致");
            return;
        }
        updateService.updatePassword($scope.entity).success(function (response) {
            if (response.success){
                alert(response.message);
                location.href="index.html";
            }else {
                alert(response.message);
            }
        });
    };
});