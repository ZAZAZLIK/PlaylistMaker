package com.practicum.playlistmaker.domain

interface UseCase<T> {
    operator fun invoke(): T
}