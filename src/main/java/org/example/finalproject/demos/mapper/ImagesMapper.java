package org.example.finalproject.demos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.finalproject.demos.pojo.Images;

@Mapper
public interface ImagesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Images record);

    int insertSelective(Images record);

    Images selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Images record);

    int updateByPrimaryKey(Images record);

    // 插入图片
    int insertImage(Images image);

    // 根据商品ID删除图片
    int deleteImagesByProductId(Integer productId);
}