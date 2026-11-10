package com.task.githubrepo.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.task.githubrepo.domain.model.TrendingRepo

@Composable
internal fun RepoRow(
    repo: TrendingRepo,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("repoRow_${repo.id}")
            .clickable(onClick = onClick)
            .animateContentSize(),
    ) {
        Row(
            modifier = Modifier.padding(start = 25.dp, top = 25.dp, bottom = if (expanded) 0.dp else 25.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = repo.ownerAvatarUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Grey),
            )
            Spacer(Modifier.width(25.dp))
            Column(Modifier.weight(1f).padding(end = 25.dp)) {
                Text(repo.name, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    repo.fullName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (expanded) {
            Column(
                modifier = Modifier
                    .testTag("details_${repo.id}")
                    .padding(start = 25.dp, end = 25.dp, top = 10.dp, bottom = 25.dp),
            ) {
                Text(
                    repo.description.orEmpty(),
                    fontSize = 14.sp,
                    color = DescriptionGrey,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(Purple200))
                    Text(repo.language ?: repo.name, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Image(StarIcon, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text(repo.stars.toString(), fontSize = 14.sp)
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(0.5.dp).background(Grey))
    }
}
