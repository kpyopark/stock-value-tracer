------- �繫��ǥ ��ǥ�ϰ� ���� �ϳ����� �ְ� ���̸� �����ִ� ��¥
-- ��ǥ : �繫��ǥ ��ǥ�ϰ� ���� �ϳ��� �ְ� ���̸� ������
-- ��� : �Ⱓ �ݺ����� ������ ����� �ϴٺ���, �ǹ̰� ����
-- ���Ĺ��� : �繫��ǥ�� ���⼺�� ���� ���� ������ ��������.
--            ��, �繫��ǥ ���⼺ ��ǥ�� +�� �ö� ���, �ְ��� +��
--            �繫��ǥ�� ���⼺ ��ǥ�� -�� �� ���, �ְ��� -�� 

select ea1.*, eb1.*, ec1.*
from   tb_company_estim_stat ea1,
       lateral (select stock_id, ea1.standard_date as stat_date, standard_date as prev_stock_date, stock_price as prev_stock_price, volume_amount as prev_volume_amount from tb_company_stock_daily a where ea1.stock_id = a.stock_id and ea1.standard_date <= a.standard_date order by a.standard_date limit 1) eb1,
       lateral (select stock_id, ea1.standard_date as stat_date, standard_date as next_stock_date, stock_price as next_stock_price, volume_amount as next_volume_amount from tb_company_stock_daily a where ea1.stock_id = a.stock_id and to_char(to_date(ea1.standard_date, 'YYYYMMDD') + INTERVAL '1 year', 'YYYYMMDD') <= a.standard_date order by a.standard_date limit 1) ec1
where  ea1.stock_id = eb1.stock_id and ea1.standard_date = eb1.stat_date
       and ea1.stock_id = ec1.stock_id and ea1.standard_date = ec1.stat_date
       and ea1.is_annual = 'Y'
       and ea1.stock_id = 'A005930'
order by ea1.stock_id, ea1.standard_date



------ ���ں� �ְ� ������ (91�� ����)
-- ��ǥ : �б⺰ �繫��ǥ ��ǥ���� �̹� �� ������ �˰� �ִ� ���� �ŷ��ڰ� �ִٰ� �Ѵٸ�
--        �и� ���ں� ���ͷ� ���̰� �ִٰ� ������ �Ǿ ������
-- ��� : ������ �и����� �ʾƼ�, �߼��� ������ ����
-- ���Ĺ��� : ���� �м��� ���� �������� ���� ��� �ʿ�.

select stock_id, day_of_period, avg(future_change_ratio) as avg, sum(future_change_ratio) as sum, count(*) as cnt
from   (
select stock_id, standard_date, cast(EXTRACT(DOY FROM to_date(standard_date, 'YYYYMMDD')) as int) % 91 as day_of_period, stock_price, volume, today_gap, next_price, next_price - stock_price as net_change, 
       log(next_price / 1.0 / stock_price) as future_change_ratio
from 
(
select stock_id, standard_date, stock_price, volume, today_high - today_low as today_gap,
       lead(stock_price, 1) over (partition by stock_id order by standard_date) as next_price
from   tb_company_stock_daily a
where  stock_price <> 0
       and stock_id < 'A200001'
order by stock_id, standard_date
) as ea
) as eea
group by stock_id, day_of_period
order by 1, 2
