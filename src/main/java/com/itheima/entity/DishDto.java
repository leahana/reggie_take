package com.itheima.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

//DTOData Transfer Object(数据传输对象)，一般用于展示层与服务层之间的数据传输。
@Data
public class DishDto extends Dish {

    //Dish
     /* {
    "name":"佛跳墙",
    "price":88800,
    "code":"",
    "image":"da9e1c70-fc32-4781-9510-a1c4ccd2ff59.jpg",
    "description":"佛跳墙",
    "status":1,
    "categoryId":"1397844357980663809",
    */
    //DishFlavor
     /*"flavors":[
        {
            "name":"辣度",
            "value":"[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]",
            "showOption":false
        },
        {
            "name":"忌口",
            "value":"[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]",
            "showOption":false
        }
    ]
}
     */
    private List<DishFlavor> flavors = new ArrayList<>();
    //菜品分类名称
    private String categoryName;

    private Integer copies;

}
