------- 재무제표 발표일과 이후 일년후의 주가 차이를 보여주는 날짜
-- 목표 : 재무제표 발표일과 이후 일년의 주가 차이를 보여줌
-- 결과 : 년간 반복으로 수익율 계산을 하다보면, 의미가 없음
-- 향후방향 : 재무제표의 방향성에 대한 측정 기준을 만들어야함.
--            즉, 재무제표 방향성 지표가 +로 올라갈 경우, 주가가 +로
--            재무제표가 방향성 지표가 -로 갈 경우, 주가가 -로 

select ea1.*, eb1.*, ec1.*
from   tb_company_estim_stat ea1,
       lateral (select stock_id, ea1.standard_date as stat_date, standard_date as prev_stock_date, stock_price as prev_stock_price, volume_amount as prev_volume_amount from tb_company_stock_daily a where ea1.stock_id = a.stock_id and ea1.standard_date <= a.standard_date order by a.standard_date limit 1) eb1,
       lateral (select stock_id, ea1.standard_date as stat_date, standard_date as next_stock_date, stock_price as next_stock_price, volume_amount as next_volume_amount from tb_company_stock_daily a where ea1.stock_id = a.stock_id and to_char(to_date(ea1.standard_date, 'YYYYMMDD') + INTERVAL '1 year', 'YYYYMMDD') <= a.standard_date order by a.standard_date limit 1) ec1
where  ea1.stock_id = eb1.stock_id and ea1.standard_date = eb1.stat_date
       and ea1.stock_id = ec1.stock_id and ea1.standard_date = ec1.stat_date
       and ea1.is_annual = 'Y'
       and ea1.stock_id = 'A005930'
order by ea1.stock_id, ea1.standard_date



------ 일자별 주가 변동성 (91일 기준)
-- 목표 : 분기별 재무재표 발표전에 이미 그 정보를 알고 있는 내부 거래자가 있다고 한다면
--        분명 일자별 수익률 차이가 있다고 생각이 되어서 조사함
-- 결과 : 패턴이 분명하지 않아서, 추세가 보이지 않음
-- 향후방향 : 패턴 분석에 대한 과학적인 접근 방법 필요.

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
