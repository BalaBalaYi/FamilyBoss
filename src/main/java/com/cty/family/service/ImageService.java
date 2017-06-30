package com.cty.family.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cty.family.dao.ImageDao;
import com.cty.family.entity.ImageEntity;


/**
 * 图像业务类
 * @author 陈天熠
 *
 */
@Service
public class ImageService {
	
	private Logger logger = LoggerFactory.getLogger(ImageService.class);
	
	// 默认图片名称   注：请保证数据库存在名为default的图片数据
	private static final String DEFAULT_IMG_NAME = "default";

	@Autowired
	private ImageDao imageDao;
	
	/**
	 * 根据id查询图像信息
	 * @param id
	 * @return
	 */
	@Cacheable(value = "image", key = "#id")
	public ImageEntity getImageById(Integer id) {
		
		ImageEntity image;
		try {
			image = imageDao.queryById(id);
		} catch (Exception e) {
			logger.error("根据id查询图像信息异常！", e);
			return null;
		}
		return image;
	}
	
	/**
	 * 根据name查询图像信息
	 * @param name
	 * @return
	 */
	@Cacheable(value = "image", key = "#name")
	public ImageEntity getImageByName(String name) {
		
		ImageEntity image;
		try {
			image = imageDao.queryByName(name);
			if(null == image){
				logger.info("根据name:" + name + " 查询图像信息为空，返回默认图像信息");
				image = imageDao.queryByName(DEFAULT_IMG_NAME);
			}
		} catch (Exception e) {
			logger.error("根据name查询图像信息异常！", e);
			return null;
		}
		return image;
	}

	/**
	 * 添加图像
	 * @param image
	 * @return
	 */
	@Transactional
	public boolean saveImage(ImageEntity image) {
		
		logger.info("待添加图像：" + image.getName());
			
		try {
			// 先查询图片是否存在
			ImageEntity imageFromDb = imageDao.queryByName(image.getName());
			if(null != imageFromDb){
				return true;
			}
			
			// 执行入库
			int addResult = imageDao.addImage(image);
			if(addResult != 1) {
				return false;
			}
		} catch (Exception e) {
			logger.error("向数据库添加图像失败！图像：" + image.getName(), e);
			return false;
		}
		return true;
	}
}

