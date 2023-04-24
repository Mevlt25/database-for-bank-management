package kullanicilar;

import java.util.Date;

public class Borclar {
	protected int bid;
	private int hid;
	private double Kredi_Borcu;
	private int Vade;
	private Date Son_�deme_Tarihi;
	private double Ayl�k_Bor�;
	
	public Borclar(int bid, int hid, double kredi_Borcu, int vade, Date son_�deme_Tarihi, double ayl�k_Bor�) {
		this.bid = bid;
		this.hid = hid;
		Kredi_Borcu = kredi_Borcu;
		Vade = vade;
		Son_�deme_Tarihi = son_�deme_Tarihi;
		Ayl�k_Bor� = ayl�k_Bor�;
	}
	
	public Borclar() {}
	

	public int getBid() {
		return bid;
	}

	public void setBid(int bid) {
		this.bid = bid;
	}

	public int getHid() {
		return hid;
	}

	public void setHid(int hid) {
		this.hid = hid;
	}

	public double getKredi_Borcu() {
		return Kredi_Borcu;
	}

	public void setKredi_Borcu(double kredi_Borcu) {
		Kredi_Borcu = kredi_Borcu;
	}

	public int getVade() {
		return Vade;
	}

	public void setVade(int vade) {
		Vade = vade;
	}

	public Date getSon_�deme_Tarihi() {
		return Son_�deme_Tarihi;
	}

	public void setSon_�deme_Tarihi(Date son_�deme_Tarihi) {
		Son_�deme_Tarihi = son_�deme_Tarihi;
	}

	public double getAyl�k_Bor�() {
		return Ayl�k_Bor�;
	}

	public void setAyl�k_Bor�(double ayl�k_Bor�) {
		Ayl�k_Bor� = ayl�k_Bor�;
	}
	
	
}
