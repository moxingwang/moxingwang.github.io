-- 导出  函数 tx_order.seq_currval 结构
DROP FUNCTION IF EXISTS `seq_currval`;
DELIMITER //
CREATE DEFINER=`dev`@`%` FUNCTION `seq_currval`(
		seq_name VARCHAR(50)
	) RETURNS varchar(255) CHARSET utf8
    DETERMINISTIC
BEGIN
		DECLARE retval VARCHAR(255);
		SET retval='-999999999';
		
		SELECT 
			concat(
				prefix,
                date_format(current_date, '%Y%m%d'),
                lpad(cast(current_value as char), number_length, '00000000')
			) 
		INTO 
			retval 
		FROM 
			tx_serial_number
		WHERE 
			serial_name = seq_name;
		RETURN retval;
	END//
DELIMITER ;


-- 导出  函数 tx_order.seq_nextval 结构
DROP FUNCTION IF EXISTS `seq_nextval`;
DELIMITER //
CREATE DEFINER=`dev`@`%` FUNCTION `seq_nextval`(
		seq_name VARCHAR(50)
	) RETURNS varchar(64) CHARSET utf8
    DETERMINISTIC
BEGIN
		UPDATE 
			tx_serial_number
		SET 
			current_value = if (
									date_format(current_date, '%Y%m%d') != date_format(now(), '%Y%m%d'), 
									0, 
									current_value + step
                                )
		WHERE 
			serial_name = seq_name;
		RETURN 
			seq_currval(seq_name);
	END//
DELIMITER ;


-- 导出  函数 tx_order.seq_setval 结构
DROP FUNCTION IF EXISTS `seq_setval`;
DELIMITER //
CREATE DEFINER=`dev`@`%` FUNCTION `seq_setval`(
		seq_name VARCHAR(50),
        value INTEGER
	) RETURNS varchar(255) CHARSET utf8
    DETERMINISTIC
BEGIN
		UPDATE 
			tx_serial_number
		SET 
			current_value = value
		WHERE 
			serial_name = seq_name;
		RETURN 
			seq_currval(seq_name);
	END//
DELIMITER ;	
