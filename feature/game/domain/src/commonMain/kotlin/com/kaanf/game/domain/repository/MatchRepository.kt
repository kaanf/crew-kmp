package com.kaanf.game.domain.repository

import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.Result
import com.kaanf.game.domain.model.AddressBook
import com.kaanf.game.domain.model.EventMemory
import com.kaanf.game.domain.model.EventParticipant
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.LeaderboardEntry
import com.kaanf.game.domain.model.MatchHistoryEntry
import com.kaanf.game.domain.model.MatchInvite
import com.kaanf.game.domain.model.MatchParticipant
import com.kaanf.game.domain.model.MatchScoreboard
import com.kaanf.game.domain.model.MatchSnapshot
import com.kaanf.game.domain.model.Quest
import com.kaanf.game.domain.model.QuestPhotoTag

interface MatchRepository {
    suspend fun getMyMatchQrToken(eventId: String): Result<String, DataError.Remote>

    /** Aktif kullanıcının katılımcı kaydını döner (QR token + userId dahil). */
    suspend fun getMyParticipant(eventId: String): Result<MatchParticipant, DataError.Remote>

    /**
     * Çağıran kullanıcının bu etkinlikteki güncel maçının snapshot'ı; aktif maç yoksa
     * `null` (sunucu 204). Soket her (yeniden) bağlandığında çağrılıp faz, kopukken
     * kaçırılan push'lara karşı sunucu doğrusuyla uzlaştırılır.
     */
    suspend fun getMatchSnapshot(eventId: String): Result<MatchSnapshot?, DataError.Remote>

    suspend fun sendInvite(
        eventId: String, scannedMatchQrToken: String,
    ): Result<MatchInvite, DataError.Remote>

    /**
     * Gelen daveti kabul eder. Sunucu iki tarafa da MATCH_STARTED push'lar; ekran geçişi
     * o soket mesajıyla sürülür, bu yüzden HTTP yanıtındaki maç gövdesi kullanılmaz.
     */
    suspend fun acceptInvite(
        eventId: String, inviteId: String,
    ): EmptyResult<DataError.Remote>

    /**
     * Gelen daveti reddeder. Sunucu daveti gönderene MATCH_INVITE_DECLINED push'lar.
     */
    suspend fun declineInvite(
        eventId: String, inviteId: String,
    ): EmptyResult<DataError.Remote>

    /**
     * Maçta "hazırım" sinyali gönderir (taş-kağıt-makas oynandıktan sonra). İki taraf da
     * hazır olduğunda sunucu iki kullanıcıya da MATCH_READY_COMPLETED push'lar; ekran geçişi
     * o soket mesajıyla sürülür.
     */
    suspend fun markReady(
        eventId: String, matchId: String,
    ): EmptyResult<DataError.Remote>

    /**
     * Maç sonucunu bildirir (I won / I lost). İlk bildiren tarafın rakibine
     * MATCH_RESULT_REPORTED; iki taraf da hemfikir olduğunda her ikisine de
     * MATCH_RESULT_CONFIRMED push'lanır. Taraflar çelişirse sunucu hata döner.
     */
    suspend fun reportResult(
        eventId: String, matchId: String, won: Boolean,
    ): EmptyResult<DataError.Remote>

    /**
     * Kazananın seçebileceği rastgele aktif görevleri döner (sunucu 3 tane verir).
     * Sunucu, event'in o anki game phase'ine göre ağırlıklı çeker.
     */
    suspend fun getTasks(eventId: String): Result<List<GameTask>, DataError.Remote>

    /**
     * Kazananın seçtiği görevi rakibe (kaybedene) sunar. Sunucu kaybedene
     * TASK_OFFERED push'lar; kaybeden bir görev onay ekranına yönlenir.
     */
    suspend fun offerTask(
        eventId: String, matchId: String, taskId: String,
    ): EmptyResult<DataError.Remote>

    /**
     * Kaybeden sunulan görevi kabul eder. Sunucu iki tarafa da TASK_STARTED push'lar;
     * geçiş o soket mesajıyla sürülür.
     */
    suspend fun acceptTask(
        eventId: String, matchId: String,
    ): EmptyResult<DataError.Remote>

    /**
     * Kaybeden sunulan görevi reddeder. Maç görev seçim adımına döner; sunucu yalnızca
     * kazanana TASK_REJECTED push'lar, böylece kazanan başka bir görev seçebilir.
     */
    suspend fun rejectTask(
        eventId: String, matchId: String,
    ): EmptyResult<DataError.Remote>

    /**
     * Kazanan, kaybedenin görevi tamamlayıp tamamlamadığını onaylar (yalnızca kazanan çağırabilir).
     * Sunucu iki tarafa da TASK_FINISHED push'lar; geçiş (puan tablosu) o soket mesajıyla sürülür.
     */
    suspend fun confirmTask(
        eventId: String, matchId: String, completed: Boolean,
    ): EmptyResult<DataError.Remote>

    /**
     * Tamamlanmış maçın puan tablosunu döner (her oyuncunun bu maçta kazandığı puanlar).
     * Puan tablosu ekranı açılınca çağrılır.
     */
    suspend fun getScoreboard(
        eventId: String, matchId: String,
    ): Result<MatchScoreboard, DataError.Remote>

    /**
     * Etkinliğin genel puan sıralamasını döner (skora göre azalan, rank dahil).
     * Oyun bitiminde leaderboard ekranı açılınca çağrılır.
     */
    suspend fun getLeaderboard(
        eventId: String,
    ): Result<List<LeaderboardEntry>, DataError.Remote>

    /**
     * Çağıran kullanıcının bu etkinlikteki biten maçları (Completed/Cancelled),
     * yeniden eskiye sıralı, sayfalı. `page` 0'dan başlar; dönen liste [size]'dan
     * kısaysa son sayfadır. "Your night" tab'ında gösterilir.
     */
    suspend fun getMatchHistory(
        eventId: String, page: Int, size: Int,
    ): Result<List<MatchHistoryEntry>, DataError.Remote>

    /**
     * Aktif (terminal olmayan) bir maçtan ayrılır = forfeit. Sunucu çağıranı kaybeden,
     * rakibi kazanan sayar ve rakibe MATCH_CANCELLED push'lar. Yalnızca devam eden maçlarda
     * çağrılmalı; biten/iptal/ret maçlarda sunucu hata döner.
     */
    suspend fun cancelMatch(
        eventId: String, matchId: String,
    ): EmptyResult<DataError.Remote>

    /**
     * Puan tablosu ekranındaki "Finish" ile maçı sonlandırır; yalnızca çağıran oyuncuyu serbest
     * bırakır (her oyuncu kendi adına çağırır). Soket push'u yoktur.
     */
    suspend fun finishMatch(
        eventId: String, matchId: String,
    ): EmptyResult<DataError.Remote>

    /**
     * Etkinliğin foto quest fotoğrafları, yeniden eskiye sayfalı (match history ile aynı
     * sözleşme: `page` 0'dan başlar, dönen liste [size]'dan kısaysa son sayfadır).
     * Oyun sürerken sunucu yalnız çağıranın yüklediklerini ve etiketlendiklerini döner;
     * etkinlik bitince tüm odanın rulosu açılır. URL'ler imzalı ve kısa ömürlü olduğundan
     * görüntülemeden önce liste tazelenmeli.
     */
    suspend fun getMemories(
        eventId: String, page: Int, size: Int,
    ): Result<List<EventMemory>, DataError.Remote>

    /**
     * Foto questine kameradan çekilen fotoğrafı gönderir (multipart). [tags] questin
     * `requiredTags` değeri kadar olmalı, çağıranı içermemeli ve pinleri 0-1 aralığında
     * olmalı; sunucu yalnız Gameplay fazında, check-in'li katılımcılara ve quest başına
     * tek fotoğrafa izin verir.
     */
    suspend fun uploadQuestPhoto(
        eventId: String,
        questKey: String,
        tags: List<QuestPhotoTag>,
        imageBytes: ByteArray,
        mimeType: String,
    ): Result<EventMemory, DataError.Remote>

    /** Etkinliğin katılımcıları; foto questinde etiket seçiminin kaynağıdır. */
    suspend fun getEventParticipants(
        eventId: String,
    ): Result<List<EventParticipant>, DataError.Remote>

    /**
     * Adres defteri: tanışılan kişiler + odadaki toplam kişi sayısı.
     * Damga pasaportu ekranı açılınca çağrılır.
     */
    suspend fun getAddressBook(eventId: String): Result<AddressBook, DataError.Remote>

    /** Katalogdaki tüm questler + çağıranın ilerleme/claim durumu. */
    suspend fun getQuests(eventId: String): Result<List<Quest>, DataError.Remote>

    /**
     * Hedefi dolmuş questin puanını alır; güncel (claimed=true) questi döner.
     * Tamamlanmamış ya da zaten alınmış quest sunucuda business hatasıyla düşer.
     */
    suspend fun claimQuest(
        eventId: String, questKey: String,
    ): Result<Quest, DataError.Remote>
}
