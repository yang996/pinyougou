//服务层
app.service("brandService", function ($http) {

    // 查询所有列表数据
    this.findAll = function () {
        return $http.get("../brand/findAll.do");
    };

    // 分页查询
    this.findPage = function (page, rows) {
        return $http.get("../brand/findPage.do?page=" + page + "&rows=" + rows);
    };

    //根据主键查询
    this.findOne = function (id) {
        return $http.get("../brand/findOne.do?id=" + id);
    };

    //批量删除信息
    this.delete = function (seleteIds) {
        return $http.get("../brand/deleteByIds.do?ids=" + seleteIds);
    };

    //多条件查询
    this.search = function (page, rows, searchEntity) {
        return $http.post("../brand/findPageByCondition.do?page=" + page + "&rows=" + rows, searchEntity);
    };

    // 新增
    this.add = function (entity) {
        return $http.post("../brand/add.do", entity);
    };

    // 更新
    this.update = function (entity) {
        return $http.post("../brand/update.do", entity);
    };
});