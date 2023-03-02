package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.appservice.MyPageRequest;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class RequestServiceImpl implements RequestService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    public RequestDto saveRequest(Long userId, RequestDto requestDto) {
        log.info("Создание запроса");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Юзер не найден"));
        requestDto.setUser(user);
        requestDto.setCreated(LocalDateTime.now());
        Request request = RequestMapper.fromDto(requestDto);
        requestRepository.save(request);
        return RequestMapper.toRequestDto(request);
    }

    @Override
    public RequestDto getRequestById(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Юзер не найден."));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Реквест не найден."));
        List<Item> itemList = itemRepository.findAllByRequestId(requestId);
        RequestDto requestDto = RequestMapper.toRequestDto(request);
        requestDto.setItems(itemList.stream().map(ItemMapper::toDto).collect(Collectors.toList()));
        return requestDto;
    }

    @Override
    public List<RequestDto> findAllByUserId(Long userId) {
        log.info("Возвращаем список реквестов и ответов на них по ид пользователя");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Юзер не найден"));
        List<Request> requestList = requestRepository.findAllByUser_IdOrderByCreatedDesc(userId);
        List<Long> idList = requestList.stream().map(Request::getId).collect(Collectors.toList());
        List<Item> itemList = itemRepository.findAllByRequestIdIn(idList);
        return getItemRequestDtosWithItems(requestList, itemList);
    }

    @Override
    public List<RequestDto> findAllFromSize(Long userId, Integer from, Integer size) {
        log.info("Возвращаем список с размером");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Юзер не найден"));
        Pageable pageRequest = MyPageRequest.makePageRequest(from, size, Sort.by(Sort.Direction.DESC, "created"));
        List<Request> requestList = requestRepository
                .findAllByUser_IdNotIn(Collections.singletonList(userId),pageRequest);
        List<Long> idList = requestList.stream().map(Request::getId).collect(Collectors.toList());
        List<Item> itemList = itemRepository.findAllByRequestIdIn(idList);
        return getItemRequestDtosWithItems(requestList, itemList);
    }

    private static List<RequestDto> getItemRequestDtosWithItems(List<Request> allItemRequests, List<Item> allItems) {
        List<RequestDto> itemRequestDtoList = allItemRequests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
        itemRequestDtoList.forEach(RequestDto ->
                RequestDto.setItems(allItems.stream()
                        .filter(item -> item.getRequest().getId().equals(RequestDto.getId()))
                        .map(ItemMapper::toDto)
                        .collect(Collectors.toList())));
        return itemRequestDtoList;
    }
}