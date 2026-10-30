# Maestro

İngilizce uygulama ve oturum açılmış test hesabı gerekir. Event başlığı benzersiz olmalı; her durum ayrı backend fixture ile çalıştırılır. Bu akış veri yokluğunda sessizce atlamaz.

```sh
maestro test -e EVENT_TITLE='Fixture title' -e EVENT_STATE=locked .maestro/e2e_events.yaml
```

EVENT_STATE: `locked` (kayıt kapalı, CTA disabled), `ended` (bitmiş, CTA disabled), `paid` (ücretli CTA açık), `free` (ücretsiz katılma → bilet → kod ekranı → geri), `ticket` (mevcut bilet → kod ekranı → geri). Free için her koşudan önce biletsiz hesap kullanın; katılım backend durumunu değiştirir. Paid ödeme gerçekleştirmez.

Ana test artık onboarding → kayıt/giriş → profil → event → profil → hesap silme sırasıyla tek akış çalışır:

```sh
maestro test -e EVENT_TITLE='Free fixture title' .maestro/e2e_onboarding_to_profile.yaml
```

Ana test varsayılan olarak `free` etkinliğe katılır ve guest list'i de kontrol eder. Yeni hesap katıldıktan sonraki toplam misafir sayısını `GUEST_COUNT`, ilk/son görünen isimleri `FIRST_GUEST_NAME` / `LAST_GUEST_NAME` ile verin; varsayılanlar yalnızca yeni hesabın tek misafir olduğu fixture içindir. `ticket` durumu yeni hesapta hazır bulunmaz; ana testte kullanmak backend üzerinden o hesaba bilet sağlanmasını gerektirir. Share/görsel parametreleri aynı komuta eklenebilir.

`e2e_events.yaml` aynı ortak alt akışı bağımsız çalıştırır; önceden giriş yapılmış hesap bekler. Her iki giriş noktası da event kontrollerinden sonra dashboard'a döner. Ana test başarı sonunda hesabı siler; ara adımda hata olursa temizlik adımına ulaşmaz.

## Fixture veya cihaz kontrolü gerektiren kalan senaryolar

Bunlar henüz otomatik doğrulanmıyor; bütün senaryoların çalıştığı iddia edilmez:

- Detay/bilet yükleme hatası ve TRY AGAIN ile toparlanma; dashboard hata/boş liste.
- Ödeme iptali, başarı, red, zaman aşımı ve tekrarlı dokunmada tek bilet.
- Kapılar açılmadan kod girişi, açılış anında otomatik geçiş.
- Kodda eksik/fazla hane, harf filtreleme, CLEAR; yanlış kod ve ardından doğru kod.
- Check-in sırasında offline/5xx, bilet bulunamadı/inaktif, check-in kapalı; tekrar deneme.
- Başarılı check-in → lobby, tekrar giriş, uygulama yeniden açılışı ve event bitişi.
- Android/iOS geri hareketi, küçük ekran, büyük font ve farklı locale.

Bu durumları deterministik test etmek için kontrol edilebilir saat, backend hata fixture'ları, geçerli/geçersiz kodlar ve platform erişilebilirlik ağacının cihaz üzerinde doğrulanması gerekir.

## Share, guest list ve görseller

Biletsiz fixture'larda guest list kilidi kontrol edilir. `ticket` için `GUEST_COUNT` zorunludur: boş liste için `0`; dolu liste için ayrıca `FIRST_GUEST_NAME` ve `LAST_GUEST_NAME` (benzersiz görünen isimler) verin. Kaydırmayı sınamak için ekrana sığmayan bir liste kullanın. İsimler regex olarak yorumlanır. Ticket fixture aktif ve kayıt kilidi açık olmalı.

```sh
maestro test -e EVENT_TITLE='Fixture title' -e EVENT_STATE=ticket -e GUEST_COUNT=0 .maestro/e2e_events.yaml
```

`CHECK_SHARE=true` ile share açma/iptal/detaya dönüş çalışır. Geçerli share URL gerekir. `SHARE_SHEET_SELECTOR` hedef Android/iOS paylaşım panelinde yalnızca panel açıkken görünen bir etikettir; iOS ayrıca `SHARE_DISMISS_SELECTOR` ister. Etiketleri `maestro hierarchy` ile alın. Android sistem geri tuşuyla kapatır. Panel içeriği veya başka uygulamaya gönderim doğrulanmaz.

`CHECK_IMAGES=true` için en az iki görselli fixture ve hedef cihazda `IMAGE_OPEN_POINT` / `IMAGE_CLOSE_POINT` (ör. `50%,25%`) gerekir. Akış açma, iki yönde swipe, ekran görüntüleri ve kapatma kontrolü yapar. Görsellerin erişilebilir sayfa etiketi olmadığı için sayfanın gerçekten değiştiği otomatik doğrulanmaz; `event-image-open` ve `event-image-next` çıktıları görsel inceleme içindir. Koordinatları viewport'a göre belirleyin.

Share ve görsel kontrolleri varsayılan kapalıdır; cihazdan seçici/koordinat alınarak açıkça etkinleştirilmelidir. Yeni akışlar cihaz üzerinde henüz doğrulanmadı.

## Gerçek backend ile maç (Maestro + rakip botu)

```sh
python3 tools/maestro/run_match.py
```

Gerekli ortam değişkenleri:

- `MATCH_API_BASE`: test backend API kökü; HTTPS ve `/api` gibi mevcut prefix dahil.
- `MATCH_EVENT_ID`: oyun başlamış, bitmemiş test etkinliği.
- `MATCH_PLAYER_TOKEN`: telefonda oturum açmış A hesabının geçerli access tokenı.
- `MATCH_BOT_TOKEN`: ayrı B hesabının geçerli access tokenı.
- `MATCH_DEVICE`: isteğe bağlı Maestro cihaz ID'si.
- `MATCH_TIMEOUT`: backend durum bekleme sınırı (varsayılan 45 saniye).

Tokenları komut argümanına veya repoya yazmayın; ortam üzerinden sağlayın. Script login/refresh yapmaz; koşu boyunca geçerli token gerekir.

Her iki hesap da aynı etkinlikte biletli ve check-in yapmış olmalı. A uygulamada `Find your next opponent.` ekranında ve event WebSocket'ine bağlı olmalı; ilk kullanım coachmark'ları önceden kapatılmalı. B'de açık maç veya bekleyen davet bulunmamalı. İki hesabın aktif maçları script tarafından ön kontrolden geçirilir. Fixture en az bir PHOTO dışı görev içermeli.

Runner sırasıyla: A'nın gerçek QR tokenını API'den alır → B adına davet gönderir → Maestro A'da kabul edip hazır olur → B hazır olur → A kaybettiğini bildirir, B kazandığını bildirir → B görev seçer → Maestro A'da görev ekranını doğrular → B tamamlandı onayı verir → Maestro scoreboard ve Finish sonrası ana oyun ekranını doğrular → B maçı bitirir.

Bot HTTP snapshot durumlarını süre sınırlı polling ile bekler. A uygulamasının gerçek WebSocket mesajlarıyla ilerlemesi gerekir; script sahte socket mesajı üretmez. Kamera/QR çözümleme test edilmez: B'nin A'yı taradığı yön, gerçek QR tokenıyla API üzerinden başlatılır. Fotoğraf, red, dispute, reconnect ve A'nın kazandığı yön bu ilk senaryoya dahil değildir.

Bu runner ayrı bir giriş noktasıdır; otomatik kayıt akışına henüz bağlı değildir (iki önceden hazırlanmış hesabın tokenları gerekir). `.maestro/match` klasörünü doğrudan toplu çalıştırmayın: adımlar arasındaki bot işlemlerini Python runner yönetir. Test kullanıcılarının puanları/maç geçmişi değişir. Hata olursa otomatik iptal/hesap silme yapılmaz; fixture sıfırlanmalı. Gerçek backend/cihaz koşusu henüz yapılmadı; doğrulananlar Python derlemesi ve Maestro sözdizimidir.

### Yeni kayıt + STRV Test Event + bot

`python3 tools/maestro/run_full_match.py` bot e-postasını ve parolasını interaktif ister (parola gizlenir). Alternatif olarak `MATCH_BOT_EMAIL` ve `MATCH_BOT_PASSWORD` ortam değişkenlerini okur. İlk verilen hesap bot olarak kullanılabilir; yeni oyuncu ana testin ortak `account_setup.yaml` alt akışıyla oluşturulur. Runner STRV ve test sözcüklerini içeren tek etkinliği seçer; bot için ücretsiz katılım/check-in yapar, yeni kullanıcıyı UI üzerinden etkinliğe alır ve maç runner'ını çağırır. Başarılı maç sonunda yalnızca yeni oluşturulan hesap uygulamadaki Delete my account ile silinir; karşılama ekranı ve clearState kullanmadan yeniden açılışta oturumun kapalı kaldığı doğrulanır; `MATCH_KEEP_PLAYER=true` ile inceleme için korunabilir. Bot hesabı silinmez. Ana onboarding testi kendi normal hesap silme adımını korur.

Cihazı `MATCH_DEVICE` ile açıkça seçin. Kod girişindeki dokunma noktası (`50%,35%`) bağlı 1440×3120 Android cihazın alan sınırlarından alınmıştır; farklı viewport için uyarlama gerekebilir. Yeni hesap coachmark'ı varsa Skip ile kapatılır. Gerçek kamera taraması hâlâ kapsam dışıdır. Başarısız koşuda yeni hesabı/maçı kontrol edip fixture'ı temizleyin. Bu tam runner'ın cihaz koşusu tamamlanmadan uçtan uca başarı iddiasında bulunulamaz.


2026-10-29 cihaz doğrulaması: bağlı Android cihazında yeni hesap oluşturuldu, STRV Test Event'e katıldı ve check-in yaptı. Bildirim paneli ve ekran seçicileri düzeltilerek aynı hesapla devam edildi; bot daveti, kabul, hazır olma, oyuncunun kaybettiğini bildirmesi, botun görev seçmesi/onaylaması, scoreboard ve Finish sonrası QR ekranına dönüş başarıyla geçti. Runner çıkış kodu 0; başarılı maç sonrasında yeni hesap silme çağrısı tamamlandı. Bu doğrulama ara hatalardan sonra aynı hesapla devam edilerek yapıldı; sıfırdan kesintisiz tam koşu henüz tekrar edilmedi. iOS/kamera doğrulanmadı.

Ara hatadan sonra devam için `MATCH_PLAYER_EMAIL` yalnızca runner'ın ürettiği `maestro+<rakamlar>@example.com` hesaplarını kabul eder; o hesabın cihazda oturumu açık olmalıdır. Başarılı koşuda varsayılan silme bu hesap için de geçerlidir.


Temizlik güncellemesi: `subflows/delete_account.yaml` silmeden önce profil e-postasını doğrular, UI üzerinden siler ve uygulamayı durdurup yerel verileri koruyarak açar. Ana onboarding testi ve maç runner'ı aynı temizliği kullanır. Önceki API silme koşusundaki 0 çıkış kodu yerel oturumun temizlendiğini kanıtlamıyordu. Yeni UI temizlik adımı henüz cihazda çalıştırılmadı; daha önce API'den silinmiş ama uygulamada açık kalan oturum bu silme senaryosu için geçerli fixture değildir.
