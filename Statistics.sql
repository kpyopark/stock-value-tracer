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

------ 통계자료를 만들기 전에, 이동평균 및, ATR을 계산한 테이블이 필요
------
CREATE TABLE public.tb_company_stock_daily_ext AS
SELECT *
FROM   (
	SELECT EEA.*,
	  ROUND(avg(variation_average_1) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE DESC ROWS BETWEEN 0 PRECEDING AND 4 FOLLOWING)) as variation_average_5, 
	  ROUND(avg(variation_average_1) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE DESC ROWS BETWEEN 0 PRECEDING AND 19 FOLLOWING)) as variation_average_20 
	FROM   (
	      SELECT EA.*,
		     ROUND(AVG(volume) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE DESC ROWS BETWEEN 0 PRECEDING AND 4 FOLLOWING)) AS volume_average_5,
		     ROUND(AVG(volume) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE DESC ROWS BETWEEN 0 PRECEDING AND 19 FOLLOWING)) AS volume_average_20,
		     GREATEST(today_high- today_low, abs(stock_price - net_change - today_high), abs(stock_price - net_change - today_low)) as variation_average_1
		FROM TB_COMPANY_STOCK_DAILY EA
	  ) EEA
       ) EA

CREATE UNIQUE INDEX CONCURRENTLY TB_COMPANY_STOCK_DAILY_EXT_PK ON tb_company_stock_daily_ext(STOCK_ID, STANDARD_DATE);

-- 위에서 TB_COMPANY_STOCK_DAILY가 추가되는 경우, tb_company_stock_daily_ext 테이블에는 어떻게 값을 추가하지?


------ 통계자료를 만드는 기본 QUERY
-- 문제가 되는 것은 항상 예전부터 느끼는 거지만, - 로 값이 변할 경우, 어떤 비율을 통해서 값을 측정해야 되는가이다.
-- 현재, SALES 값 조차도 -가 발생하여, 위 내용을 해결하지 않는 이상, 깔끔하게 비율로 표시할 수가 없다.

SELECT STOCK_ID, 
       STANDARD_DATE, 
       stock_price, 
       net_change, 
       net_change_ratio, 
       ask_price, 
       bid_price, 
       today_high, 
       today_low, 
       volume, 
       volume_amount, 
       open_price, 
       par_value, 
       currency, 
       ordinary_share, 
       market_capital, 
       security_type, 
       eps, cps, bps, sps, 
       epp, bpp, cpp, spp, 
       roa, 
       row_number() over (partition by standard_date order by epp desc NULLS LAST) as epp_ranking,
       row_number() over (partition by standard_date order by bpp desc NULLS LAST) as bpp_ranking, 
       row_number() over (partition by standard_date order by cpp desc NULLS LAST) as cpp_ranking, 
       row_number() over (partition by standard_date order by spp desc NULLS LAST) as spp_ranking, 
       row_number() over (partition by standard_date order by roa desc NULLS LAST) as roa_ranking,
       sales_inc_ratio_ ^ 2 as sales_inc_ratio, 
       sales_inc_ratio_4_ as sales_inc_ratio_4, 
       |/ sales_inc_ratio_8_ as sales_inc_ratio_8, 
       |/ |/ sales_inc_ratio_16_ as sales_inc_ratio_16 
from   (
SELECT A.STOCK_ID, A.STANDARD_DATE, 
       stock_price, 
       net_change, 
       net_change_ratio, 
       ask_price, 
       bid_price, 
       today_high, 
       today_low, 
       volume, 
       volume_amount, 
       open_price, 
       par_value, 
       currency, 
       ordinary_share, 
       market_capital, 
       security_type, 
       net_profit / ordinary_share as eps, 
       ORDINARY_PROFIT / ordinary_share as cps , 
       CAPITAL_TOTAL / ordinary_share as bps, 
       SALES / ordinary_share as sps, 
       (net_profit / ordinary_share)::float / stock_price as epp, 
       (CAPITAL_TOTAL / ordinary_share)::float / stock_price as bpp, 
       (ORDINARY_PROFIT / ordinary_share)::float / stock_price as cpp, 
       (SALES / ordinary_share)::float / stock_price as spp, 
       (net_profit::float / ASSET_TOTAL) as roa, 
       ASSET_TOTAL, DEBT_TOTAL, CAPITAL, CAPITAL_TOTAL, SALES, OPERATION_PROFIT, ORDINARY_PROFIT, NET_PROFIT,
       case when sales_1q_before > 0 then (sales::float / sales_1q_before) else 0 end as sales_inc_ratio_, 
       case when sales_4q_before > 0 then (sales::float / sales_4q_before) else 0 end as sales_inc_ratio_4_, 
       case when sales_8q_before > 0 then (sales::float / sales_8q_before) else 0 end as sales_inc_ratio_8_, 
       case when sales_16q_before > 0 then (sales::float / sales_16q_before) else 0 end as sales_inc_ratio_16_, 
       sales_1q_before, 
       sales_4q_before, 
       sales_8q_before, 
       sales_16q_before
  FROM TB_COMPANY_STOCK_DAILY A, 
       LATERAL (SELECT STOCK_ID, MAX(STANDARD_DATE) AS STANDARD_DATE FROM TB_COMPANY_ESTIM_STAT B WHERE A.STOCK_ID = B.STOCK_ID AND A.STANDARD_DATE >= B.REGISTERED_DATE GROUP BY STOCK_ID) EB
       JOIN (
           SELECT IC.*,
	       LAG(SALES,1) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE) as sales_1q_before, 
	       LAG(SALES,4) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE) as sales_4q_before,
	       LAG(SALES,8) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE) as sales_8q_before,
	       LAG(SALES,16) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE) as sales_16q_before
             FROM TB_COMPANY_ESTIM_STAT IC) C ON (EB.STOCK_ID = C.STOCK_ID AND EB.STANDARD_DATE = C.STANDARD_DATE)
WHERE  A.STANDARD_DATE = '20160414'
) x


---------
---- 특정 기간에 발생한 상관계수의 변화를 확인해본다. 
---- 이련 상관계수와 주간 상관계수의 변화를 향후 비교해 본다.
---- 평균과 표준편차를 이용해서 큰 변화없는 상관관계가 있는 종목을 확인해본다.

WITH T_LOG_SAMPLE AS (
SELECT A. STOCK_ID, STANDARD_DATE, 
       LOG(CASE WHEN STOCK_PRICE > 0 AND (STOCK_PRICE-NET_CHANGE) > 0 THEN STOCK_PRICE::FLOAT/(STOCK_PRICE-NET_CHANGE) ELSE 1 END) AS LOG_CHANGE
  FROM TB_COMPANY_STOCK_DAILY A JOIN (SELECT STOCK_ID FROM TB_COMPANY_AND_DEFFERED WHERE CLOSE_YN = 'N' AND FUTURE_YN = 'Y') B ON (A.STOCK_ID = B.STOCK_ID)
WHERE  STANDARD_DATE BETWEEN '20130101' AND '20131231'
)
SELECT X, Y, AVG(CORR_EFF), STDDEV(CORR_EFF)
FROM   (
	SELECT A.STOCK_ID AS X, B.STOCK_ID AS Y, SUBSTR(A.STANDARD_DATE,1,6), CORR(A.LOG_CHANGE, B.LOG_CHANGE) AS CORR_EFF
	FROM   T_LOG_SAMPLE A JOIN T_LOG_SAMPLE B ON (A.STANDARD_DATE = B.STANDARD_DATE)
	WHERE  1=1
	GROUP BY A.STOCK_ID, B.STOCK_ID, SUBSTR(A.STANDARD_DATE,1,6)
	HAVING CORR(A.LOG_CHANGE, B.LOG_CHANGE) <> 1
	ORDER BY CORR_EFF ASC
       ) T_A
GROUP BY X, Y
ORDER BY ABS(AVG(CORR_EFF)) - STDDEV(CORR_EFF) DESC


----------
----- 기존 자료가지고 통계만들기.
-----

INSERT INTO tb_company_stock_daily_ext 
SELECT *
FROM   (
	SELECT EEA.*,
	  ROUND(avg(variation_average_1) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE DESC ROWS BETWEEN 0 PRECEDING AND 4 FOLLOWING)) as variation_average_5, 
	  ROUND(avg(variation_average_1) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE DESC ROWS BETWEEN 0 PRECEDING AND 19 FOLLOWING)) as variation_average_20 
	FROM   (
	      SELECT EA.*,
		     ROUND(AVG(volume) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE DESC ROWS BETWEEN 0 PRECEDING AND 4 FOLLOWING)) AS volume_average_5,
		     ROUND(AVG(volume) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE DESC ROWS BETWEEN 0 PRECEDING AND 19 FOLLOWING)) AS volume_average_20,
		     GREATEST(today_high- today_low, abs(stock_price - net_change - today_high), abs(stock_price - net_change - today_low)) as variation_average_1
		FROM TB_COMPANY_STOCK_DAILY EA
	  ) EEA
       ) EA
 WHERE NOT EXISTS (SELECT 1 FROM tb_company_stock_daily_ext OE WHERE OE.STOCK_ID = EA.STOCK_ID AND OE.STANDARD_DATE = EA.STANDARD_DATE)

 ----------
 -----
 -----
 INSERT INTO TB_DAILY_STAT_FACTOR
SELECT STOCK_ID, 
       STANDARD_DATE, 
       stock_price, 
       net_change, 
       net_change_ratio, 
       ask_price, 
       bid_price, 
       today_high, 
       today_low, 
       volume, 
       volume_amount, 
       open_price, 
       par_value, 
       currency, 
       ordinary_share, 
       market_capital, 
       security_type, 
       eps, cps, bps, sps, 
       epp, bpp, cpp, spp, 
       roa, 
       row_number() over (partition by standard_date order by epp desc NULLS LAST) as epp_ranking,
       row_number() over (partition by standard_date order by bpp desc NULLS LAST) as bpp_ranking, 
       row_number() over (partition by standard_date order by cpp desc NULLS LAST) as cpp_ranking, 
       row_number() over (partition by standard_date order by spp desc NULLS LAST) as spp_ranking, 
       row_number() over (partition by standard_date order by roa desc NULLS LAST) as roa_ranking,
       sales_inc_ratio_ ^ 2 as sales_inc_ratio, 
       sales_inc_ratio_4_ as sales_inc_ratio_4, 
       |/ sales_inc_ratio_8_ as sales_inc_ratio_8, 
       |/ |/ sales_inc_ratio_16_ as sales_inc_ratio_16, 
       oper_profit_inc_ratio_ ^ 2 as oper_profit_inc_ratio, 
       oper_profit_inc_ratio_4_ as oper_profit_inc_ratio_4, 
       |/ oper_profit_inc_ratio_8_ as oper_profit_inc_ratio_8, 
       |/ |/ oper_profit_inc_ratio_16_ as oper_profit_inc_ratio_16, 
       0 as op_profit_ratio, 
       net_profit_ratio, 
       0 as eps_inc_ratio, 
       DEBT_TOTAL / ASSET_TOTAL as debt_ratio, 
       CAPITAL_TOTAL / ASSET_TOTAL as capt_ratio, 
       0 as borrowing_dependency, 
       volume_average_20, 
       volume_average_5, 
       variation_average_1, 
       variation_average_5, 
       variation_average_20, 
       0 short_ratio, 
       0 sell_buy_ratio, 
       0 bollinger, 
       0 kospi_beta_average_52, 
       0 kosdak_beta_average_52, 
       0 major_ratio, 
       0 foreigner_buy_amount, 
       0 institution_buy_amount, 
       0 eps_variation_ratio, 
       0 atr
from   (
SELECT A.STOCK_ID, 
       A.STANDARD_DATE, 
       stock_price, 
       net_change, 
       net_change_ratio, 
       ask_price, 
       bid_price, 
       today_high, 
       today_low, 
       volume, 
       volume_amount, 
       open_price, 
       par_value, 
       currency, 
       ordinary_share, 
       market_capital, 
       security_type, 
       net_profit / ordinary_share as eps, 
       ORDINARY_PROFIT / ordinary_share as cps , 
       CAPITAL_TOTAL / ordinary_share as bps, 
       SALES / ordinary_share as sps, 
       (net_profit / ordinary_share)::float / stock_price as epp, 
       (CAPITAL_TOTAL / ordinary_share)::float / stock_price as bpp, 
       (ORDINARY_PROFIT / ordinary_share)::float / stock_price as cpp, 
       (SALES / ordinary_share)::float / stock_price as spp, 
       (net_profit::float / ASSET_TOTAL) as roa, 
       ASSET_TOTAL, DEBT_TOTAL, CAPITAL, CAPITAL_TOTAL, SALES, OPERATION_PROFIT, ORDINARY_PROFIT, NET_PROFIT,
       case when (SALES > 0  AND sales_1q_before > 0) then (sales::float / sales_1q_before) else 0 end as sales_inc_ratio_, 
       case when (SALES > 0  AND sales_4q_before > 0) then (sales::float / sales_4q_before) else 0 end as sales_inc_ratio_4_, 
       case when (SALES > 0  AND sales_8q_before > 0) then (sales::float / sales_8q_before) else 0 end as sales_inc_ratio_8_, 
       case when (SALES > 0  AND sales_16q_before > 0) then (sales::float / sales_16q_before) else 0 end as sales_inc_ratio_16_, 
       case when (OPERATION_PROFIT > 0 and oper_profit_1q_before > 0) then (OPERATION_PROFIT::float / oper_profit_1q_before) else 0 end as oper_profit_inc_ratio_, 
       case when (OPERATION_PROFIT > 0 and oper_profit_4q_before > 0) then (OPERATION_PROFIT::float / oper_profit_4q_before) else 0 end as oper_profit_inc_ratio_4_, 
       case when (OPERATION_PROFIT > 0 and oper_profit_8q_before > 0) then (OPERATION_PROFIT::float / oper_profit_8q_before) else 0 end as oper_profit_inc_ratio_8_, 
       case when (OPERATION_PROFIT > 0 and oper_profit_16q_before > 0) then (OPERATION_PROFIT::float / oper_profit_16q_before) else 0 end as oper_profit_inc_ratio_16_, 
       case when (NET_PROFIT > 0 and net_profit_1q_before > 0) then (NET_PROFIT::float / net_profit_1q_before) else 0 end as net_profit_ratio,
       volume_average_5,
       volume_average_20,
       variation_average_1, 
       variation_average_5, 
       variation_average_20 
  FROM tb_company_stock_daily_ext A, 
       LATERAL (SELECT STOCK_ID, MAX(STANDARD_DATE) AS STANDARD_DATE FROM TB_COMPANY_ESTIM_STAT B WHERE A.STOCK_ID = B.STOCK_ID AND A.STANDARD_DATE >= B.REGISTERED_DATE GROUP BY STOCK_ID) EB
       JOIN (
           SELECT IC.*,
	       LAG(SALES,1) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE) as sales_1q_before, 
	       LAG(SALES,4) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE) as sales_4q_before,
	       LAG(SALES,8) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE) as sales_8q_before,
	       LAG(SALES,16) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE) as sales_16q_before,
	       LAG(OPERATION_PROFIT,1) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE) as oper_profit_1q_before, 
	       LAG(OPERATION_PROFIT,4) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE) as oper_profit_4q_before,
	       LAG(OPERATION_PROFIT,8) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE) as oper_profit_8q_before,
	       LAG(OPERATION_PROFIT,16) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE) as oper_profit_16q_before,
	       LAG(NET_PROFIT,1) OVER (PARTITION BY STOCK_ID ORDER BY STANDARD_DATE) as net_profit_1q_before 
             FROM TB_COMPANY_ESTIM_STAT IC) C ON (EB.STOCK_ID = C.STOCK_ID AND EB.STANDARD_DATE = C.STANDARD_DATE)
) T_X
 WHERE NOT EXISTS (SELECT 1 FROM TB_DAILY_STAT_FACTOR T_Y WHERE T_X.STOCK_ID = T_Y.STOCK_ID AND T_X.STANDARD_DATE = T_Y.STANDARD_DATE)
 