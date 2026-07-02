package com.kaanf.game.domain.repository

import com.kaanf.core.domain.util.DataError
import com.kaanf.core.domain.util.EmptyResult
import com.kaanf.core.domain.util.Result
import com.kaanf.game.domain.model.GameTask
import com.kaanf.game.domain.model.MatchInvite
import com.kaanf.game.domain.model.MatchParticipant
import com.kaanf.game.domain.model.MatchScoreboard
import com.kaanf.game.domain.model.MatchSnapshot

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
     * Maçtan bağımsız global katalogdan gelir.
     */
    suspend fun getTasks(): Result<List<GameTask>, DataError.Remote>

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
}
