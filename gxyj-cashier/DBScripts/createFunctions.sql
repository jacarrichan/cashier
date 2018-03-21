-- ----------------------------
-- Function structure for child_mall_parentids
-- ----------------------------
DROP FUNCTION IF EXISTS child_mall_parentids;

CREATE FUNCTION child_mall_parentids(p_mallid VARCHAR(32)) RETURNS VARCHAR(4000) CHARSET utf8
BEGIN 
    DECLARE sTemp VARCHAR(4000); 
    DECLARE sTempChd VARCHAR(2000);
   
    DECLARE v_depth INT DEFAULT 0;
    
    SET sTemp= NULL;

    SET sTempChd = '';

  	WHILE sTempChd IS NOT NULL DO 
      IF (v_depth=0) THEN
        SELECT GROUP_CONCAT(DISTINCT parent_id) INTO sTempChd FROM csr_mall_info WHERE parent_id=p_mallid;
      ELSEIF (v_depth=1) THEN
        SELECT GROUP_CONCAT(DISTINCT parent_id) INTO sTempChd FROM csr_mall_info WHERE parent_id IN(
          SELECT mall_id FROM csr_mall_info WHERE parent_id=p_mallid
        );
      ELSEIF (v_depth=2) THEN
        SELECT GROUP_CONCAT(DISTINCT parent_id) INTO sTempChd FROM csr_mall_info WHERE parent_id IN(
          SELECT mall_id FROM csr_mall_info WHERE parent_id IN(
            SELECT mall_id FROM csr_mall_info WHERE parent_id=p_mallid
          )
        );
      ELSEIF (v_depth=3) THEN
        SELECT GROUP_CONCAT(DISTINCT parent_id) INTO sTempChd FROM csr_mall_info WHERE parent_id IN(
          SELECT mall_id FROM csr_mall_info WHERE parent_id IN(
            SELECT mall_id FROM csr_mall_info WHERE parent_id IN(
              SELECT mall_id FROM csr_mall_info WHERE parent_id=p_mallid
            )
          )
        );
      ELSEIF (v_depth=4) THEN
        SELECT GROUP_CONCAT(DISTINCT parent_id) INTO sTempChd FROM csr_mall_info WHERE parent_id IN(
          SELECT mall_id FROM csr_mall_info WHERE parent_id IN(
            SELECT mall_id FROM csr_mall_info WHERE parent_id IN(
              SELECT mall_id FROM csr_mall_info WHERE parent_id IN(
                SELECT mall_id FROM csr_mall_info WHERE parent_id=p_mallid
              )
            )
          )
        );
      ELSEIF (v_depth=5) THEN
        #if path is 6 break the loop
        SET sTempChd = NULL;
      END IF;
      
      IF sTempChd IS NOT NULL THEN
        IF (sTemp IS NULL) THEN
          SET sTemp = sTempChd;
        ELSE
          SET sTemp = CONCAT(sTemp,',',sTempChd); 
        END IF;
      END IF;
      
      SET v_depth = v_depth + 1;
      
    END WHILE;
    
    RETURN sTemp; 
  END;



-- ----------------------------
-- Function structure for currval
-- ----------------------------
DROP FUNCTION IF EXISTS currval;

CREATE FUNCTION currval(v_seq_name VARCHAR(50)) RETURNS int(11)
BEGIN
	declare value INTEGER;
    declare flag integer;
    set value=0;
    set flag=0;
    
SELECT 
    COUNT(seq_name)
INTO flag FROM
    sequence
WHERE
    seq_name = v_seq_name;
    if flag<=0  then
		INSERT INTO sequence (seq_name, current_val, increment_val) VALUES (v_seq_name, 1, 1);
	 end if;
     
SELECT 
    current_val
INTO value FROM
    sequence
WHERE
    seq_name = v_seq_name;
   
   return value;
END;

-- ----------------------------
-- Function structure for nextval
-- ----------------------------
DROP FUNCTION IF EXISTS nextval;

CREATE FUNCTION nextval(v_seq_name VARCHAR(50)) RETURNS int(11)
begin
    update sequence set current_val = current_val + increment_val  where seq_name = v_seq_name;
    return currval(v_seq_name);
end;