package kullanicilar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

public class Musteri extends Kullanici{
    Statement st = null;
    ResultSet rs = null;
    PreparedStatement ps = null;
    private int cid;
    
	public Musteri(int id, String Ad_Soyad, String Telefon, String TC_No, String Adres, String Eposta, int cid) {
		super(id, Ad_Soyad, Telefon, TC_No, Adres, Eposta);
		this.cid = cid;
	}

	public Musteri() {}
	
	public ArrayList<Hesap> hesapListesi() throws SQLException, ClassNotFoundException{
        ArrayList<Hesap> list = new ArrayList<>();
        Hesap hesap;
        try {   
        	Connection con = conn.conDB();       	
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM Hesaplar Where mid = '"+this.id+"'");
            
            while(rs.next()){
            	hesap = new Hesap(rs.getInt("hid"),rs.getInt("mid"),rs.getString("Para_Birimi"),rs.getDouble("Bakiye"),rs.getDouble("Gelir"),rs.getDouble("Gider"));
                list.add(hesap);
            }
            con.close(); st.close(); rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;     
    }
	
	public ArrayList<Borclar> borcListesi() throws SQLException, ClassNotFoundException{
        ArrayList<Borclar> list = new ArrayList<>();
        Borclar borc;
        try {   
        	Connection con = conn.conDB();       	
            st = con.createStatement();
            rs = st.executeQuery("SELECT b.bid,b.hid,b.Kredi_Borcu,b.Vade,b.Son_�deme_Tarihi,b.Ayl�k_Bor�\r\n"
            		+ "FROM Hesaplar h, Bor�lar b\r\n"
            		+ "WHERE b.hid = h.hid AND h.mid = '"+this.id+"'");
            
            while(rs.next()){
            	borc = new Borclar(rs.getInt("bid"),rs.getInt("hid"),rs.getDouble("Kredi_Borcu"),rs.getInt("Vade"),rs.getDate("Son_�deme_Tarihi"),rs.getDouble("Ayl�k_Bor�"));
                list.add(borc);
            }
            con.close(); st.close(); rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;     
    }
	
	public ArrayList<Islem> islemListesi() throws SQLException, ClassNotFoundException{
        ArrayList<Islem> list = new ArrayList<>();
        Islem islem;
        try {   
        	Connection con = conn.conDB();       	
            st = con.createStatement();
            rs = st.executeQuery("SELECT DISTINCT i.�slem_No,i.Kaynak_ID,i.Hedef_ID,i.�slem,i.Tutar,i.Kaynak_Bakiye,i.Hedef_Bakiye,i.Tarih FROM islemler i, musteriler m,hesaplar h \r\n"
            		+ "WHERE (i.Hedef_ID = h.hid or\r\n"
            		+ "	i.Kaynak_ID = h.hid) and\r\n"
            		+ "	h.mid = m.mid and\r\n"
            		+ "	h.mid = '"+this.id+"'");

            while(rs.next()){
            	islem = new Islem(rs.getInt("�slem_No"),rs.getInt("Kaynak_ID"),rs.getInt("Hedef_ID"),rs.getString("�slem"),rs.getDouble("Tutar"),rs.getInt("Kaynak_Bakiye"),rs.getInt("Hedef_Bakiye"),rs.getString("Tarih"));
                list.add(islem);
            }
            con.close(); st.close(); rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;     
    }
	
	public Kur kurFarkiBul(String pb1, String pb2) throws ClassNotFoundException{
		Kur kur = null;
		
        try {   
        	Connection con = conn.conDB();       	
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM Kur Where Para_Birimi = '"+pb1+"' and Para_Birimi2 = '"+pb2+"'");
            
            while(rs.next()){
                kur = new Kur(rs.getString("Para_Birimi"),rs.getString("Para_Birimi2"),rs.getDouble("Kur_De�eri"));                  
            }
  
            con.close(); st.close(); rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
		return kur;
	}
	
	public boolean hesapGuncelle(int id,double miktar,int kontrol) throws SQLException, ClassNotFoundException {
		String query = null;
		double kredi = 0;
		int vade = 0;
		Date tarih = null;
		double aylikBorc = 0;
		
		if(kontrol == 0) {
			query = "UPDATE Hesaplar SET Bakiye = '"+(hesapBul(id).getBakiye()-miktar)+"', Gider += '"+miktar+"' Where hid = '"+id+"'";
		}
		else if(kontrol == 1) {
			query = "UPDATE Hesaplar SET Bakiye = '"+(hesapBul(id).getBakiye()+miktar)+"', Gelir += '"+miktar+"' Where hid = '"+id+"'";
		}
		else if(kontrol == 2) {
			kredi = borcBul(id).getKredi_Borcu() - (borcBul(id).getKredi_Borcu()/borcBul(id).getVade());
			vade = borcBul(id).getVade() - 1;
			tarih = borcBul(id).getSon_�deme_Tarihi();
			tarih.setDate(borcBul(id).getSon_�deme_Tarihi().getDate()+30);
			
			if(vade != 0) {
				if(miktar == borcBul(id).getAyl�k_Bor�()) {
					aylikBorc = (kredi/vade)+((kredi/vade)*degerListesi().get(0).getFaizOran�()/100);		
				}		
				else {
					aylikBorc = (kredi/vade)+((kredi/vade)*degerListesi().get(0).getFaizOran�()/100) + (borcBul(id).getAyl�k_Bor�()-miktar) + ((borcBul(id).getAyl�k_Bor�()-miktar)*degerListesi().get(0).getGecikmeFaizOran�())/100;	
				}	
			}

			query = "UPDATE Hesaplar SET Bakiye -= '"+miktar+"', Gider += '"+miktar+"' WHERE hid = '"+id+"'\r\n"
					+"UPDATE Bor�lar SET Kredi_Borcu = '"+kredi+"', Vade = '"+vade+"', Son_�deme_Tarihi = '"+tarih+"', Ayl�k_Bor� = '"+aylikBorc+"' WHERE hid = '"+id+"'\r\n"
					+"UPDATE Hesaplar SET Bakiye += '"+miktar+"', Gelir += '"+miktar+"' WHERE hid = '0'";			
		}
		else if(kontrol == 3) {
			kredi = borcBul(id).getKredi_Borcu() - (borcBul(id).getKredi_Borcu()/borcBul(id).getVade());
			vade = borcBul(id).getVade() - 1;
			tarih = borcBul(id).getSon_�deme_Tarihi();
			tarih.setDate(borcBul(id).getSon_�deme_Tarihi().getDate()+30);
			
			if(vade != 0)
				aylikBorc = (kredi/vade)+((kredi/vade)*degerListesi().get(0).getGecikmeFaizOran�()/100);
			
			query = "UPDATE Hesaplar SET Bakiye -= '"+miktar+"', Gider += '"+miktar+"' WHERE hid = '"+id+"'\r\n"
					+"UPDATE Bor�lar SET Kredi_Borcu = '"+kredi+"', Vade = '"+vade+"', Son_�deme_Tarihi = '"+tarih+"', Ayl�k_Bor� = '"+aylikBorc+"' WHERE hid = '"+id+"'\r\n"
					+"UPDATE Hesaplar SET Bakiye += '"+miktar+"', Gelir += '"+miktar+"' WHERE hid = '0'";	
		}
		else if(kontrol == 4) {
			kredi = borcBul(id).getKredi_Borcu() - (borcBul(id).getKredi_Borcu()/borcBul(id).getVade());
			vade = borcBul(id).getVade() - 1;
			tarih = borcBul(id).getSon_�deme_Tarihi();
			tarih.setDate(borcBul(id).getSon_�deme_Tarihi().getDate()+30);
			
			if(vade != 0)
				aylikBorc = kredi/vade;
			
			query = "UPDATE Hesaplar SET Bakiye -= '"+miktar+"', Gider += '"+miktar+"' WHERE hid = '"+id+"'\r\n"
					+"UPDATE Bor�lar SET Kredi_Borcu = '"+kredi+"', Vade = '"+vade+"', Son_�deme_Tarihi = '"+tarih+"', Ayl�k_Bor� = '"+aylikBorc+"' WHERE hid = '"+id+"'\r\n"
					+"UPDATE Hesaplar SET Bakiye += '"+miktar+"', Gelir += '"+miktar+"' WHERE hid = '0'";			
		}
		else if(kontrol == 5) {
			query = "UPDATE Hesaplar SET Bakiye -= '"+miktar+"', Gider += '"+miktar+"' WHERE hid = '"+id+"'\r\n"
					+"DELETE Bor�lar WHERE hid = '"+id+"'\r\n"
					+"UPDATE Hesaplar SET Bakiye += '"+miktar+"', Gelir += '"+miktar+"' WHERE hid = '0'";
		}
			
		Connection con;
		boolean key = false;
		
		try {
			con = conn.conDB();
			st = con.createStatement();
			ps = con.prepareStatement(query);
			ps.execute();
			key = true;
			
			con.close(); st.close(); ps.close();			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	
		if(key)
			return true;
		else
			return false;
	}
	
	public boolean musteriGuncelle(int mid,String ad, String tel, String tc, String adres, String eposta) throws SQLException {
		String query = "UPDATE Musteriler SET Ad_Soyad='"+ad+"' ,Telefon='"+tel +"' ,TC_No='"+tc+"' ,Adres='"+adres+"' ,Eposta='"+eposta+"'WHERE mid='"+mid+"'";
		Connection con;
		boolean key = false;

		try {
			con = conn.conDB();
			st = con.createStatement();
			ps = con.prepareStatement(query);
			ps.execute();
			key = true;
			
			con.close(); st.close(); ps.close();			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	
		if(key)
			return true;
		else
			return false;
	}
	
	public boolean islemEkle(int id, int id2, double miktar, int durum) throws SQLException {
		String query = "INSERT INTO �slemler(Kaynak_ID,Hedef_ID,Tutar,�slem,Kaynak_Bakiye,Hedef_Bakiye,Tarih) VALUES (?,?,?,?,?,?,?)";
		Connection con;
		boolean key = false;
		try {
			con = conn.conDB();
			st = con.createStatement();
			ps = con.prepareStatement(query);
			if(durum == 0) {
				ps.setInt(1, id);
				ps.setInt(2, id2);
				ps.setDouble(3, miktar);
				ps.setString(4, "Para �ekme");
				ps.setDouble(5, hesapBul(id).getBakiye());
				ps.setDouble(6, hesapBul(id2).getBakiye());
				ps.setString(7, degerListesi().get(0).getTarih().toString());
			}
			else if(durum == 1) {
				ps.setInt(1, id2);
				ps.setInt(2, id);
				ps.setDouble(3, miktar);
				ps.setString(4, "Para Yat�rma");
				ps.setDouble(5, hesapBul(id2).getBakiye());
				ps.setDouble(6, hesapBul(id).getBakiye());
				ps.setString(7, degerListesi().get(0).getTarih().toString());
			}
			else if(durum == 2) {
				query = "INSERT INTO �slemler(Kaynak_ID,Hedef_ID,Tutar,�slem,Kaynak_Bakiye,Hedef_Bakiye,Tarih) VALUES (?,?,?,?,?,?,?)";
				ps.setInt(1, id);
				ps.setInt(2, 0);
				ps.setDouble(3, miktar);
				ps.setDouble(5, hesapBul(id).getBakiye());
				ps.setDouble(6, hesapBul(0).getBakiye());
				ps.setString(7, degerListesi().get(0).getTarih().toString());
				
				if(id2 == 1)
					ps.setString(4, "Kredi Borcu �deme");
				else if(id2 == 2)
					ps.setString(4, "Kredi Borcu Ge� �deme");
				else if(id2 == 3)
					ps.setString(4, "Kredi Borcu Erken �deme");
			}
			else if(durum == 3) {
				ps.setInt(1, id);
				ps.setInt(2, id2);
				ps.setDouble(3, miktar);
				ps.setString(4, "Para Transferi");
				ps.setDouble(5, hesapBul(id).getBakiye());
				ps.setDouble(6, hesapBul(id2).getBakiye());
				ps.setString(7, degerListesi().get(0).getTarih().toString());
			}
			else if(durum == 4) {
				ps.setInt(1, id);
				ps.setInt(2, id2);
				ps.setDouble(3, miktar);
				ps.setString(4, "T�m Kredi Borcunu �deme");
				ps.setDouble(5, hesapBul(id).getBakiye());
				ps.setDouble(6, hesapBul(id2).getBakiye());
				ps.setString(7, degerListesi().get(0).getTarih().toString());
			}
			else if(durum == 6) {
				ps.setInt(1, 0);
				ps.setInt(2, 0);
				ps.setDouble(3, miktar);
				ps.setString(4, "�al��an �cretlerinin Yat�r�lmas�");
				ps.setDouble(5, hesapBul(0).getBakiye());
				ps.setDouble(6, 0);
				ps.setString(7, degerListesi().get(0).getTarih().toString());
			}
			else if(durum == 7) {
				query = "INSERT INTO �slemler(Kaynak_ID,Hedef_ID,Tutar,�slem,Kaynak_Bakiye,Hedef_Bakiye,Tarih) VALUES (?,?,?,?,?,?,?)";
	
				ps.setInt(1, id);
				ps.setInt(2, 0);
				ps.setDouble(3, miktar);
				ps.setString(4, "Kredi Borcu Faiz �deme");
				ps.setDouble(5, hesapBul(id).getBakiye());
				ps.setDouble(6, hesapBul(0).getBakiye());
				ps.setString(7, degerListesi().get(0).getTarih().toString());
			}

			ps.executeUpdate();
			key = true;

			con.close(); st.close(); ps.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	
		if(key)
			return true;
		else
			return false;
	}
	
	public boolean paraTransferi(int kaynak,int hedef,double miktar) throws SQLException, ClassNotFoundException {
		String query = null;
		double kurfarki = 1;
		
		if(!hesapBul(kaynak).getPara_Birimi().equals(hesapBul(hedef).getPara_Birimi()))
			kurfarki = kurFarkiBul(hesapBul(kaynak).getPara_Birimi(),hesapBul(hedef).getPara_Birimi()).getKurfarki();
	
		query = "UPDATE Hesaplar SET Bakiye = '"+(hesapBul(kaynak).getBakiye()-miktar)+"', Gider += '"+miktar+"' WHERE hid = '"+kaynak+"'\r\n"
				+ "UPDATE Hesaplar SET Bakiye = '"+(hesapBul(hedef).getBakiye()+miktar*kurfarki)+"', Gelir += '"+miktar+"' WHERE hid = '"+hedef+"'";
		
		Connection con;
		boolean key = false;
		
		try {
			con = conn.conDB();
			st = con.createStatement();
			ps = con.prepareStatement(query);
			ps.execute();
			key = true;
			
			con.close(); st.close(); ps.close();			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	
		if(key)
			return true;
		else
			return false;
	}
	
	public boolean borcSil(int hid) throws SQLException, ClassNotFoundException {
		String query = "DELETE Bor�lar WHERE hid = '"+hid+"'";
	
		Connection con;
		boolean key = false;
		
		try {
			con = conn.conDB();
			st = con.createStatement();
			ps = con.prepareStatement(query);
			ps.execute();
			key = true;
			
			con.close(); st.close(); ps.close();			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	
		if(key)
			return true;
		else
			return false;
	}
	
	public boolean talepEkle(int id, int hid, int miktar,int vade,String parabirimi, int durum) throws SQLException {
		String query = "INSERT INTO Talepler(mid,Talep,hid,Miktar,Vade,Para_Birimi) VALUES (?,?,?,?,?,?)";
		Connection con;
		boolean key = false;
		try {
			con = conn.conDB();
			st = con.createStatement();
			ps = con.prepareStatement(query);
			
			if(durum == 0) {
				ps.setInt(1, id);
				ps.setString(2, "Hesap Silme");
				ps.setInt(3, hid);
				ps.setInt(4, miktar);
				ps.setInt(5, 0);
				ps.setString(6, hesapBul(hid).getPara_Birimi());
			}
			else if(durum == 1) {
				ps.setInt(1, id);
				ps.setString(2, "Yeni Hesap Olu�turma");
				ps.setInt(3, hid);
				ps.setInt(4, miktar);
				ps.setInt(5, 0);
				ps.setString(6, parabirimi);
			}
			else if(durum == 2) {
				ps.setInt(1, id);
				ps.setString(2, "Kredi Talebi");
				ps.setInt(3, hid);
				ps.setInt(4, miktar);
				ps.setInt(5, vade);
				ps.setString(6, hesapBul(hid).getPara_Birimi());
			}

			ps.executeUpdate();
			key = true;

			con.close(); st.close(); ps.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	
		if(key)
			return true;
		else
			return false;
	}
	
	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}
	
}
