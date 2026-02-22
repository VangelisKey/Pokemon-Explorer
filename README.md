# Pokemon Explorer App

A modern Android application built to explore the Pokemon universe using the PokeAPI. This app allows users to browse Pokemon by specific types, search by name, and dive into detailed stats and evolution chains.

## Features

- Type Selection: Filter Pokemon by the 10 requested types: Fire, Water, Grass, Electric, Dragon, Psychic, Ghost, Dark, Steel, and Fairy, plus an "All" option.
- Search Functionality: Search for a Pokemon by name within a selected type. Includes a debounced search implementation to prevent unnecessary API spam when typing.
- Paginated Display: Displays Pokemon in a responsive grid. Loads 10 Pokemon at a time, with a Load More mechanism to fetch or reveal additional results.
- Detailed Pokemon View: Tapping a Pokemon reveals its full details, including Official Artwork, Name, Types, and Base Stats: HP, Attack, and Defense.
- Graceful Error Handling: Implemented a robust Resource wrapper to handle Loading, Success, and Error states. The UI gracefully displays friendly error messages and provides Try Again mechanisms.
- Evolution Chains: The detail screen makes additional API calls to fetch and display the Pokemon's complete, clickable evolution chain.

## Tech Stack & Architecture

- UI: Jetpack Compose for a fully declarative user interface.
- Architecture: MVVM combined with Clean Architecture principles separated into Domain, Data, and UI layers.
- Dependency Injection: Dagger Hilt for scalable dependency management.
- Networking: Retrofit2 and Gson for interacting with the PokeAPI.
- Asynchronous Programming: Kotlin Coroutines and StateFlow for handling background tasks and observing UI state.
- Image Loading: Coil for asynchronous image downloading and caching.
- Navigation: Navigation Compose for seamless transitions between the List and Detail screens.

## Technical Highlights

- Smart Pagination: Because the API's type endpoint does not support standard offset/limit pagination natively, the app fetches the type list once and intelligently pages the results locally 10 at a time. For the "All" category, it utilizes true remote API pagination.
- Dynamic Theming: The UI dynamically adapts its color palette based on the selected Pokemon type.