package post;

public enum EnumFutureOptionCategory {
	
	FOTYPE_KOSPI200( "KRDRVOPK2I", "코스피200 옵션"),
	FOTYPE_MINISP200( "KRDRVOPMKI", "미니코스피200 옵션"),
	FOTYPE_SAMSUM( "KRDRVOPS11", "삼성전자 옵션"),
	FOTYPE_SKTEL( "KRDRVOPS12", "SK텔레콤 옵션"),
	FOTYPE_POSCO( "KRDRVOPS13", "POSCO 옵션"),
	FOTYPE_KT( "KRDRVOPS14", "KT 옵션"),
	FOTYPE_KOPOW( "KRDRVOPS15", "한국전력 옵션"),
	FOTYPE_HYUNDAI( "KRDRVOPS16", "현대차 옵션"),
	FOTYPE_KIA( "KRDRVOPS19", "기아차 옵션"),
	FOTYPE_MOBIS( "KRDRVOPS20", "현대모비스 옵션"),
	FOTYPE_LGE( "KRDRVOPS24", "LG전자 옵션"),
	FOTYPE_HHI( "KRDRVOPS39", "현대중공업 옵션"),
	FOTYPE_HANA( "KRDRVOPS40", "하나금융지주 옵션"),
	FOTYPE_SKI( "KRDRVOPS41", "SK이노베이션 옵션"),
	FOTYPE_LGD( "KRDRVOPS45", "LG디스플레이 옵션"),
	FOTYPE_KBE( "KRDRVOPS46", "KB금융 옵션"),
	FOTYPE_LGC( "KRDRVOPS47", "LG화학 옵션"),
	FOTYPE_SKH( "KRDRVOPS50", "SK하이닉스 옵션"),
	FOTYPE_LGU( "KRDRVOPSB0", "LG유플러스 옵션"),
	FOTYPE_SDS( "KRDRVOPSC5", "삼성SDS 옵션"),
	FOTYPE_AMORE( "KRDRVOPSC6", "아모레퍼시픽 옵션"),
	FOTYPE_SAMI( "KRDRVOPSC8", "삼성물산 옵션"),
	FOTYPE_DOLLAR( "KRDRVOPUSD", "미국달러 옵션");
	
	String code;
	String categoryName;
	
	private EnumFutureOptionCategory(String code, String categoryName) {
		this.code = code;
		this.categoryName = categoryName;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public String getCategoryName() {
		return this.categoryName;
	}
	
	
}
