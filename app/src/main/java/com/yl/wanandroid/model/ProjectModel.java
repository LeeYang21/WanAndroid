package com.yl.wanandroid.model;

import com.yl.wanandroid.base.BaseModel;
import com.yl.wanandroid.service.dto.Articles;
import com.yl.wanandroid.service.dto.ProjectCategory;
import com.yl.wanandroid.service.interfaces.ErrorListener;
import com.yl.wanandroid.service.interfaces.ResponseListener;

import java.util.List;

public class ProjectModel extends BaseModel {
    public void getProjectCategory(ResponseListener<List<ProjectCategory>> listener, ErrorListener errorListener) {
        rxService.add(apiService.getProjectCategory(), listener, errorListener);
    }

    public void getProjectList(int index, int id, ResponseListener<Articles> listener, ErrorListener errorListener) {
        rxService.add(apiService.getProjectList(index, id), listener, errorListener);
    }
}
