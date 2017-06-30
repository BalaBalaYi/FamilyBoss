package com.cty.family.dao;

import org.apache.ibatis.annotations.Mapper;

import com.cty.family.entity.ImageEntity;

@Mapper
public interface ImageDao {

	/**
	 * 根据图像id查询图像
	 * @param id
	 * @return
	 */
	public ImageEntity queryById(Integer id);
	
	/**
	 * 根据图像name查询图像
	 * @param name
	 * @return
	 */
	public ImageEntity queryByName(String name);
	
	/**
	 * 添加图像
	 * @param image
	 * @return
	 */
	public int addImage(ImageEntity image);
	
	
}
