package com.nbd.ocp.core.repository.crud;
/*
                       _ooOoo_
                      o8888888o
                      88" . "88
                      (| -_- |)
                      O\  =  /O
                   ____/`---'\____
                 .'  \\|     |//  `.
                /  \\|||  :  |||//  \
               /  _||||| -:- |||||-  \
               |   | \\\  -  /// |   |
               | \_|  ''\---/''  |   |
               \  .-\__  `-`  ___/-. /
             ___`. .'  /--.--\  `. . __
          ."" '<  `.___\_<|>_/___.'  >'"".
         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
         \  \ `-.   \_ __\ /__ _/   .-` /  /
    ======`-.____`-.___\_____/___.-`____.-'======
                       `=---='
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
             佛祖保佑       永无BUG
*/


import com.nbd.ocp.core.repository.base.IOcpBaseController;
import com.nbd.ocp.core.repository.exception.service.ServiceException;
import com.nbd.ocp.core.repository.request.OcpQueryPageBaseVo;
import com.nbd.ocp.core.repository.response.OcpJsonResponse;
import com.nbd.ocp.core.repository.utils.OcpGenericsUtils;
import com.nbd.ocp.core.repository.utils.OcpSpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author jin
 */

public interface IOcpCrudBaseController<T extends IOcpCrudBaseDo,I extends IOcpCrudBaseService> extends IOcpBaseController<T,I> {
    Logger logger = LoggerFactory.getLogger(IOcpCrudBaseController.class);
    default I getCrudBaseService(){
        return (I) OcpSpringUtil.getBean(OcpGenericsUtils.getControllerSuperClassGenericsType(getClass(), IOcpCrudBaseService.class));
    }
    @RequestMapping(value = "add",method = RequestMethod.POST)
    @ResponseBody
    default OcpJsonResponse add(@RequestBody T t) {
        T r= (T) getCrudBaseService().save(t);
        return OcpJsonResponse.success("新增成功",r);
    }
    @RequestMapping(value = "/delete/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    default OcpJsonResponse delete(@PathVariable("id")  String id) {
        getCrudBaseService().deleteById(id);
        return OcpJsonResponse.success("删除成功");
    }
    @RequestMapping(value = "update",method = RequestMethod.PUT)
    @ResponseBody
    default OcpJsonResponse update(@RequestBody T t) {
        T r= (T) getCrudBaseService().updateSelective(t);
        return OcpJsonResponse.success("更新成功",r);
    }
    @RequestMapping(value = "/get/{id}",method = RequestMethod.GET)
    @ResponseBody
    default OcpJsonResponse getById(@PathVariable("id")  String id) {
        T r= (T) getCrudBaseService().getById( id);
        return OcpJsonResponse.success("查询成功",r);
    }

    @RequestMapping(value = "/page",method = RequestMethod.GET)
    @ResponseBody
    default OcpJsonResponse page(OcpQueryPageBaseVo ocpQueryPageBaseVo) {
        Page<T> r=getCrudBaseService().page(ocpQueryPageBaseVo);
        return OcpJsonResponse.success("查询成功",r);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    default OcpJsonResponse list(OcpQueryPageBaseVo queryBaseVo) {
        List<T> r =getCrudBaseService().list(queryBaseVo);
        return OcpJsonResponse.success("查询成功",r);
    }
}
