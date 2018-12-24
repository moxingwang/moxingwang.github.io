# 错误
> [22001][1292] Data truncation: Truncated incorrect DOUBLE value: '10059-A06-T7-00104'

# sql
````aidl
UPDATE rem_booth_info a, rem_product_info b
SET a.measured_area = round(measured_area * shared_rate, 3)
WHERE a.product_id = b.id AND b.invest_ref_id = 1 AND a.shared_rate != 1;
````

# 原因
> 字段类型不正确

# 修改
```aidl
UPDATE rem_booth_info a, rem_product_info b
SET a.measured_area = round(measured_area * shared_rate, 3)
WHERE a.product_id = b.id AND b.invest_ref_id = '1' AND a.shared_rate != 1;
```