package com.thejufo.chat.ui.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thejufo.chat.R
import com.thejufo.chat.data.models.Chat
import com.thejufo.chat.data.models.Contact
import com.thejufo.chat.ui.theme.ChatTheme
import com.thejufo.chat.ui.theme.white

@Composable
fun ContactsScreen(
  viewModel: ContactsViewModel = hiltViewModel(),
  onStartChat: (Chat) -> Unit = {},
  onNavigateUp: () -> Unit = {}
) {

  val contacts by viewModel.contacts.collectAsStateWithLifecycle()
  val chatCreated by viewModel.chatCreated.collectAsStateWithLifecycle()

  val showNewGroupDialog = remember { mutableStateOf(false) }

  LaunchedEffect(chatCreated) {
    if (chatCreated != null) {
      onStartChat(chatCreated!!)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.background,
        title = {
          Text(text = "Contacts")
        },
        navigationIcon = {
          IconButton(onNavigateUp) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(onClick = {
            showNewGroupDialog.value = true
          }, modifier = Modifier.padding(end = 12.dp)) {
            Icon(
              painter = painterResource(R.drawable.ic_group_add),
              contentDescription = "New Group"
            )
          }
        }
      )
    }
  ) { padding ->
    LazyColumn(
      Modifier
        .padding(padding)
    ) {
      items(items = contacts, key = { it.jid }) { contact ->
        ContactItem(contact = contact) {
          viewModel.startConversation(it.name)
        }
      }
    }
  }

  if (showNewGroupDialog.value) {
    NewGroupDialog(
      contacts = contacts,
      onCreateGroup = { participants, name ->
        viewModel.createGroup(participants, name)
      },
      onDismissDialog = {
        showNewGroupDialog.value = false
      })
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContactItem(
  contact: Contact,
  onClick: (Contact) -> Unit
) {

  var showProgress by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier
      .fillMaxWidth(),
    elevation = 0.dp,
    onClick = {
      showProgress = true
      onClick(contact)
    }
  ) {

    Row(
      Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Icon(
        modifier = Modifier.size(40.dp),
        tint = Color.Black.copy(alpha = 0.25f),
        painter = painterResource(R.drawable.ic_account),
        contentDescription = ""
      )
      Text(text = contact.name, modifier = Modifier.weight(1f))

      if (showProgress) {
        CircularProgressIndicator()
      }
    }
  }
}

@Composable
fun NewGroupDialog(
  contacts: List<Contact>,
  onCreateGroup: (participants: List<Contact>, groupName: String) -> Unit,
  onDismissDialog: () -> Unit
) {

  var groupName by remember { mutableStateOf("") }
  val participants = remember { mutableListOf<Contact>() }

  var creating by remember { mutableStateOf(false) }

  Dialog(
    content = {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(0.dp, 400.dp),
        shape = RoundedCornerShape(8.dp),
        color = white,
      ) {
        Column(
          modifier = Modifier
            .background(white)
            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp),
        ) {
          OutlinedTextField(
            enabled = !creating,
            modifier = Modifier
              .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
              backgroundColor = white,
            ),
            value = groupName,
            onValueChange = { groupName = it },
            placeholder = {
              Text("Group Name")
            })

          Spacer(modifier = Modifier.height(24.dp))
          Text("Participants", fontWeight = FontWeight.SemiBold)
          Spacer(modifier = Modifier.height(8.dp))
          Divider()
          LazyColumn(
            modifier = Modifier.weight(1f)
          ) {
            items(contacts, key = { it.jid }) { contact ->
              val checked = remember { mutableStateOf(false) }
              LaunchedEffect(checked.value) {
                if (checked.value) {
                  participants.add(contact)
                } else {
                  participants.remove(contact)
                }
              }
              Row(
                verticalAlignment = Alignment.CenterVertically
              ) {
                Checkbox(
                  enabled = !creating,
                  checked = checked.value,
                  onCheckedChange = { checked.value = it })
                Text(contact.name)
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
          Row(
            Modifier
              .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.End),
          ) {
            TextButton(
              enabled = !creating,
              onClick = onDismissDialog
            ) {
              Text("CANCEL")
            }
            Button(
              enabled = !creating,
              onClick = {
                if (groupName.isNotEmpty()) {
                  creating = true
                  onCreateGroup(participants, groupName)
                }
              }) {
              Text("CREATE")
            }
          }
        }
      }
    },

    onDismissRequest = { /*TODO*/ },
  )
}

@Preview
@Composable
fun ContactsScreenPreview() {
  ChatTheme {
    ContactsScreen()
  }
}