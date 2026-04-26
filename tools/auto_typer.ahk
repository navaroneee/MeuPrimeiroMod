#Requires AutoHotkey v2.0
; ============================================================================
; AUTO TYPER — digita conteúdo de arquivo char por char (pra gravação de vídeo)
;
; USO:
;   1. Ajuste o FOLDER abaixo pra pasta onde ficam seus snippets .txt
;   2. Abra o IntelliJ, posicione o cursor onde quer escrever
;   3. Aperte F1 (abre popup pra escolher arquivo) OU F2..F8 (snippets rápidos)
;   4. Aguarde 3 segundos e a digitação começa
;
; HOTKEYS:
;   F1          → escolher arquivo via diálogo
;   F2 ... F8   → carregar snippet_2.txt ... snippet_8.txt direto
;   Esc         → CANCELA a digitação em andamento
;   Pause/Break → pausa/retoma a digitação
;   F9          → ajustar velocidade (+/-)
; ============================================================================

; --- CONFIGURAÇÃO ---
FOLDER := "E:\Minecraft\forge\1.20\tools\snippets"
DELAY_MIN := 50        ; delay mínimo entre chars (ms)
DELAY_MAX := 130       ; delay máximo (variação humana)
PAUSE_AFTER_NEWLINE := 250   ; pausa após quebra de linha
PAUSE_AFTER_BLANK := 700     ; pausa após linha em branco (novo bloco)
PAUSE_AFTER_SEMICOLON := 180 ; pausa extra após ';' (fim de statement)
PAUSE_AFTER_OPEN_BRACE := 350 ; pausa após '{' (pensa no conteúdo)
PAUSE_AFTER_CLOSE_BRACE := 250 ; pausa após '}'
PAUSE_AFTER_COMMA := 100      ; pausa curta após ',' (próximo argumento)
THINKING_CHANCE := 8          ; % de chance de pausa "pensando" por char
THINKING_MIN := 300           ; pausa pensando mínima
THINKING_MAX := 900           ; pausa pensando máxima
TYPO_CHANCE := 0              ; % de chance de typo+correção (0 = off; 3-5 = humano)
STARTUP_DELAY := 3000         ; tempo antes de começar
STRIP_AUTO_INDENT := true     ; apaga auto-indent do IntelliJ após Enter

; --- ESTADO ---
global typing := false
global paused := false
global cancelRequested := false
global speedMultiplier := 1.0

; --- HOTKEYS ---
F1::ChooseFile()
F2::LoadSnippet(2)
F3::LoadSnippet(3)
F4::LoadSnippet(4)
F5::LoadSnippet(5)
F6::LoadSnippet(6)
F7::LoadSnippet(7)
F8::LoadSnippet(8)

Esc::{
    global cancelRequested, typing
    if typing {
        cancelRequested := true
        TrayTip("AutoTyper", "Cancelado", 1)
    }
}

Pause::{
    global paused
    paused := !paused
    TrayTip("AutoTyper", paused ? "Pausado" : "Retomado", 1)
}

F9::AdjustSpeed()

; --- FUNÇÕES ---
ChooseFile() {
    file := FileSelect(1, FOLDER, "Escolha o snippet", "Texto (*.txt)")
    if (file != "")
        StartTyping(file)
}

LoadSnippet(n) {
    file := FOLDER "\snippet_" n ".txt"
    if FileExist(file)
        StartTyping(file)
    else
        TrayTip("AutoTyper", "Arquivo não encontrado:`n" file, 2)
}

AdjustSpeed() {
    global speedMultiplier
    result := InputBox("Velocidade atual: " speedMultiplier "x`nNova (0.5 = 2x mais rápido, 2.0 = 2x mais lento):", "AutoTyper — Velocidade", "w200 h100")
    if result.Result = "OK" {
        newSpeed := Number(result.Value)
        if (newSpeed > 0)
            speedMultiplier := newSpeed
    }
}

StartTyping(file) {
    global typing, cancelRequested, paused
    if typing {
        TrayTip("AutoTyper", "Já estou digitando!", 1)
        return
    }

    try {
        code := FileRead(file, "UTF-8")
    } catch {
        TrayTip("AutoTyper", "Erro ao ler arquivo", 2)
        return
    }

    TrayTip("AutoTyper", "Começa em " (STARTUP_DELAY / 1000) "s...", 1)
    Sleep STARTUP_DELAY

    typing := true
    cancelRequested := false
    paused := false

    TypeString(code)

    typing := false
    TrayTip("AutoTyper", "Feito!", 1)
}

TypeString(text) {
    global cancelRequested, paused, speedMultiplier

    Loop Parse, text, "`n", "`r" {
        line := A_LoopField

        while paused {
            Sleep 100
            if cancelRequested
                return
        }

        if cancelRequested
            return

        TypeLine(line)

        ; Enter + pausa extra
        Send "{Enter}"

        ; Apaga auto-indent do IDE (cursor → Home, seleciona tudo até End, Delete)
        if (STRIP_AUTO_INDENT) {
            Sleep 30
            Send "{Home}+{End}{Delete}"
            Sleep 30
        }

        extraPause := (StrLen(line) = 0) ? PAUSE_AFTER_BLANK : PAUSE_AFTER_NEWLINE
        Sleep Integer(extraPause * speedMultiplier)
    }
}

; Processa uma linha interpretando marcadores especiais {{...}}
TypeLine(line) {
    global cancelRequested, paused, speedMultiplier

    i := 1
    len := StrLen(line)
    while (i <= len) {
        if cancelRequested
            return
        while paused
            Sleep 100

        ; Detecta marcador {{...}}
        if (SubStr(line, i, 2) == "{{") {
            endPos := InStr(line, "}}", , i)
            if (endPos > 0) {
                marker := SubStr(line, i + 2, endPos - i - 2)
                ProcessMarker(marker)
                i := endPos + 2
                continue
            }
        }

        char := SubStr(line, i, 1)
        TypeChar(char)

        delay := Random(DELAY_MIN, DELAY_MAX)
        Sleep Integer(delay * speedMultiplier)

        extra := GetCharExtraDelay(char)
        if (extra > 0)
            Sleep Integer(extra * speedMultiplier)

        if (Random(1, 100) <= THINKING_CHANCE) {
            Sleep Integer(Random(THINKING_MIN, THINKING_MAX) * speedMultiplier)
        }

        i++
    }
}

; Interpreta marcadores: {{TAB}}, {{ENTER}}, {{ALT+ENTER}}, {{CTRL+SPACE}}, {{WAIT=500}}
ProcessMarker(marker) {
    global speedMultiplier

    marker := Trim(marker)
    upper := StrUpper(marker)

    ; Marcador de espera: {{WAIT=500}}
    if (SubStr(upper, 1, 5) == "WAIT=") {
        ms := Integer(SubStr(marker, 6))
        Sleep Integer(ms * speedMultiplier)
        return
    }

    ; Teclas/combos
    switch upper {
        case "TAB":
            ; Força autocomplete + aceita com TAB (importa a classe)
            Sleep Integer(200 * speedMultiplier)
            Send "^{Space}"
            Sleep Integer(450 * speedMultiplier)  ; espera popup carregar
            Send "{Tab}"
            Sleep Integer(150 * speedMultiplier)
        case "RAWTAB":
            ; TAB bruto (só pressiona Tab, sem autocomplete)
            Send "{Tab}"
        case "ENTER":
            Sleep Integer(300 * speedMultiplier)
            Send "{Enter}"
        case "ALT+ENTER", "ALT-ENTER":
            Sleep Integer(200 * speedMultiplier)
            Send "!{Enter}"
            Sleep Integer(500 * speedMultiplier)
        case "CTRL+SPACE", "CTRL-SPACE":
            Sleep Integer(150 * speedMultiplier)
            Send "^{Space}"
            Sleep Integer(400 * speedMultiplier)
        case "ESC":
            Send "{Escape}"
        case "BACKSPACE":
            Send "{Backspace}"
        default:
            Send "{" marker "}"
    }

    Sleep Integer(150 * speedMultiplier)  ; respiro após ação
}

GetCharExtraDelay(char) {
    switch char {
        case ";": return PAUSE_AFTER_SEMICOLON
        case "{": return PAUSE_AFTER_OPEN_BRACE
        case "}": return PAUSE_AFTER_CLOSE_BRACE
        case ",": return PAUSE_AFTER_COMMA
    }
    return 0
}

TypeChar(char) {
    SendText(char)
}
