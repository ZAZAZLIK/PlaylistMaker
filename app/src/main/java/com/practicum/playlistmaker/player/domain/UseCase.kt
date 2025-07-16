package com.practicum.playlistmaker.player.domain

interface UseCase<T> {
    operator fun invoke(): T
}