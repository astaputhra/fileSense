package com.iMatch.etl.exceptions;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: shreyas
 * Date: 2/7/14
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class HexgenUIException extends RuntimeException {

    private static final long serialVersionUID = -828191690485918339L;
    private String errorMessage;

    public HexgenUIException(String message){
        super(message);
        this.errorMessage = message;
    }

    public ModelAndView asModelAndView() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("$ERROR_TYPE", "SIMPLE_UI_ERROR_MESSAGE");
        map.put("$ERROR_MESSAGE", errorMessage);
        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        return new ModelAndView(jsonView, map);
    }
}
