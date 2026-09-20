# TG WS Proxy - общайся без ограничений в любимом самолетике

Magisk модуль для ускорения работы Telegram через WebSocket + Cloudflare прокси.

Форк [Flowseal/tg-ws-proxy](https://github.com/Flowseal/tg-ws-proxy), движок на Rust от [valnesfjord/tg-ws-proxy-rs](https://github.com/valnesfjord/tg-ws-proxy-rs).

## Как работает

Telegram → локальный MTProto (127.0.0.1:1443) → tg-ws-proxy → WSS через Cloudflare → Telegram DC

## Установка

1. Скачай apk из [Releases](https://github.com/f1ndles/tg-ws-proxy/releases)
2. Запусти установленное приложение и нажми на кнопку посередине и потом "Применить в Telegram"
3. В самом Telegram нажми на кнопку подкючения прокси
4. можно пользоваться телеграмм 

## Настройки

| Параметр | Описание |
|----------|----------|
| CF Domain | Твой Cloudflare домен |
| Default Domains | Автозагрузка рабочих CF доменов с GitHub |
| CF Priority | CF прокси идёт до прямого WS |
| CF Balance | Балансировка между CF доменами |

## Credits (отдельная благодарность)

- [Flowseal/tg-ws-proxy](https://github.com/Flowseal/tg-ws-proxy) — оригинал
- [valnesfjord/tg-ws-proxy-rs](https://github.com/valnesfjord/tg-ws-proxy-rs) — Rust движок
- Александр К - многочисленная поддержка проектов
- АВТОР - многочисленная поддержка проектов
# Поддержка

2200701799637712 - (8027 -12К. На новый мобильник, т.к мой уже еле живёт) Спасибо всем за поддержку!

