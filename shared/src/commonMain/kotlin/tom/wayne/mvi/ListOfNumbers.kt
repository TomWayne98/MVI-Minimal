package tom.wayne.mvi

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.random.Random

class NumbersViewModel {

    // Store který obsahuje téma zobrazené na obrazovce ať už to jsou Čísla jako zde, MyBets, Ticket detail nebo cokoliv.
    private val store: NumbersStore = NumbersStoreFactory(DefaultStoreFactory()).create()

    // Obsahuje všechny data k vykreslení na obrazovku.
    val state: Flow<NumbersStore.NumberState> = store.state()
    // Jediná cesta jak komunikuje View část se zbytkem.
    fun emitIntent(intent: NumbersIntent) = store.emitIntent(intent)
}

// Action je funguje stejně jako Intent
// Jediný rozdíl je že Action spouští aplikace a ne uživatel
// Například když chceme začít stahovat něco hned při vstupu na obrazovku
private sealed class NumbersAction {
    class PrepareBasicList(val n: Int) : NumbersAction()
}

// Obsahuje všechny subkomponenty jako je Executor, Reducer, Bootstrapper atd.
// Activita/Fragment/moderator do něj posílá akce uživatele a konzumuje State
interface NumbersStore {

    data class NumberState(val numbers: MutableList<Int> = mutableListOf())

    fun emitIntent(intent: NumbersIntent)

    fun state(): Flow<NumberState>
}

// Made by user - such as AddToTicketAction or MakeNewBetAction
sealed class NumbersIntent {
    // Přidá číslo do listu
    object Add : NumbersIntent()
    // Odebere číslo z listu
    object Remove : NumbersIntent()
    // Přidat specifické číslo - na to jsem tlačítko v UI nedělal
    data class AddSpecific(val value: Int) : NumbersIntent()
}

// Result je výsledek který vrátí Executor
sealed class NumbersResult {
    class NewList(val numbers: List<Int>) : NumbersResult()
    class NewNumber(val number: Int) : NumbersResult()
    object RemoveNumber : NumbersResult()
}

// Faktory by tady nemuselo být vůbec. Stačil by Number Store - je to ale užitečné
// Pro tvoření Mockovacích Stores pro testy
internal class NumbersStoreFactory(private val storeFactory: StoreFactory) {

    fun create(): NumbersStore =
        object : NumbersStore, Store<NumbersIntent, NumbersStore.NumberState, Nothing> by storeFactory
            .create(
                name = "NumbersStore",
                initialState = NumbersStore.NumberState(),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl,
                bootstrapper = SimpleBootstrapper(NumbersAction.PrepareBasicList(10))
            ) {

            override fun emitIntent(intent: NumbersIntent) {
                accept(intent)
            }

            override fun state() = states
        }

    // Pokud je třeba aby něco běhalo asynchroně - je to náročné, tak to řeší executor.
    // Exekutor implementuje coroutine scope.
    // Je to v podstatě obdoba Repositary z MVVM
    private class ExecutorImpl : CoroutineExecutor<NumbersIntent, NumbersAction, NumbersStore.NumberState, NumbersResult, Nothing>() {
        // Vykonává intenty od uživatele
        override fun executeIntent(intent: NumbersIntent, getState: () -> NumbersStore.NumberState) =
            when (intent) {
                is NumbersIntent.Add -> {
                    val randomNumber = Random.nextInt(0,100)
                    dispatch(NumbersResult.NewNumber(randomNumber))
                }
                is NumbersIntent.Remove -> dispatch(NumbersResult.RemoveNumber)
                is NumbersIntent.AddSpecific -> calculateVeryDifficultNumber(intent.value)
            }

        // Vykonává akce od BootStraperu
        override fun executeAction(action: NumbersAction, getState: () -> NumbersStore.NumberState) {
            when (action) {
                is NumbersAction.PrepareBasicList -> {
                    calculateVeryDifficultNumber(action.n)
                }
            }
        }

        // Symuluje náročnou funkci
        private fun calculateVeryDifficultNumber(n: Int) {
            scope.launch {
                val basicListNumbers = mutableListOf<Int>()
                while (basicListNumbers.size <= n) {
                    val randomNumber = async(Dispatchers.Default) {
                        Random.nextInt(0, 100)
                    }.await()
                    basicListNumbers.add(randomNumber)
                }
                dispatch(NumbersResult.NewList(basicListNumbers))
            }
        }
    }

    // Je zodpovědný za update State
    // Veme předešlý stav a vrátí nový upravený stav
    private object ReducerImpl : Reducer<NumbersStore.NumberState, NumbersResult> {
        override fun NumbersStore.NumberState.reduce(result: NumbersResult): NumbersStore.NumberState =
            when (result) {
                is NumbersResult.NewList -> {
                    copy(numbers = numbers.also { nmbrs -> nmbrs.addAll(result.numbers.toList()) })
                }
                is NumbersResult.NewNumber -> {
                    copy(numbers = numbers.also { nmbrs -> nmbrs.add(result.number) })
                }
                is NumbersResult.RemoveNumber -> {
                    copy(numbers = numbers.also { nmbrs -> nmbrs.removeLast() })
                }
            }
    }
}

