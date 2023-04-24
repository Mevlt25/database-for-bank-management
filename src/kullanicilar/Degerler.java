package kullanicilar;

import java.util.Date;

public class Degerler {
	private double maas;
	private float faizOran�;
	private float gecikmeFaizOran�;
	private Date Tarih;
	
	public Degerler(double maas, float faizOran�, float gecikmeFaizOran�, Date tarih) {
		this.maas = maas;
		this.faizOran� = faizOran�;
		this.gecikmeFaizOran� = gecikmeFaizOran�;
		Tarih = tarih;
	}
	
	public Degerler() {}
	

	public Date getTarih() {
		return Tarih;
	}

	public void setTarih(Date tarih) {
		Tarih = tarih;
	}

	public double getMaas() {
		return maas;
	}

	public void setMaas(double maas) {
		this.maas = maas;
	}

	public float getFaizOran�() {
		return faizOran�;
	}

	public void setFaizOran�(float faizOran�) {
		this.faizOran� = faizOran�;
	}

	public float getGecikmeFaizOran�() {
		return gecikmeFaizOran�;
	}

	public void setGecikmeFaizOran�(float gecikmeFaizOran�) {
		this.gecikmeFaizOran� = gecikmeFaizOran�;
	}
}
