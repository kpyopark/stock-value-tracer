package post;

public enum EnumFutureOptionCategory {
	
	FOTYPE_KOSPI200( "KRDRVOPK2I", "�ڽ���200 �ɼ�"),
	FOTYPE_MINISP200( "KRDRVOPMKI", "�̴��ڽ���200 �ɼ�"),
	FOTYPE_SAMSUM( "KRDRVOPS11", "�Ｚ���� �ɼ�"),
	FOTYPE_SKTEL( "KRDRVOPS12", "SK�ڷ��� �ɼ�"),
	FOTYPE_POSCO( "KRDRVOPS13", "POSCO �ɼ�"),
	FOTYPE_KT( "KRDRVOPS14", "KT �ɼ�"),
	FOTYPE_KOPOW( "KRDRVOPS15", "�ѱ����� �ɼ�"),
	FOTYPE_HYUNDAI( "KRDRVOPS16", "������ �ɼ�"),
	FOTYPE_KIA( "KRDRVOPS19", "����� �ɼ�"),
	FOTYPE_MOBIS( "KRDRVOPS20", "������ �ɼ�"),
	FOTYPE_LGE( "KRDRVOPS24", "LG���� �ɼ�"),
	FOTYPE_HHI( "KRDRVOPS39", "�����߰��� �ɼ�"),
	FOTYPE_HANA( "KRDRVOPS40", "�ϳ��������� �ɼ�"),
	FOTYPE_SKI( "KRDRVOPS41", "SK�̳뺣�̼� �ɼ�"),
	FOTYPE_LGD( "KRDRVOPS45", "LG���÷��� �ɼ�"),
	FOTYPE_KBE( "KRDRVOPS46", "KB���� �ɼ�"),
	FOTYPE_LGC( "KRDRVOPS47", "LGȭ�� �ɼ�"),
	FOTYPE_SKH( "KRDRVOPS50", "SK���̴н� �ɼ�"),
	FOTYPE_LGU( "KRDRVOPSB0", "LG���÷��� �ɼ�"),
	FOTYPE_SDS( "KRDRVOPSC5", "�ＺSDS �ɼ�"),
	FOTYPE_AMORE( "KRDRVOPSC6", "�Ƹ��۽��� �ɼ�"),
	FOTYPE_SAMI( "KRDRVOPSC8", "�Ｚ���� �ɼ�"),
	FOTYPE_DOLLAR( "KRDRVOPUSD", "�̱��޷� �ɼ�");
	
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
